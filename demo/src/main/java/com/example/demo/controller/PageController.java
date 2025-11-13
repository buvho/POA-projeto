package com.example.demo.controller;

import com.example.demo.model.Classroom;
import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.service.*;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    private final UserService userService;
    private final ClassroomService classroomService;
    
    public PageController(UserService userService, ClassroomService classroomService) {
        this.userService = userService;
        this.classroomService = classroomService;
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("user", new User());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute User user,
                            @RequestParam String type,
                            @RequestParam(required = false) String matricula) {

        if (type.equalsIgnoreCase("student")) {
            Student student = new Student();
            student.setName(user.getName());
            student.setEmail(user.getEmail());
            student.setPassword(user.getPassword());
            student.setMatricula(matricula);
            userService.cadastrar(student, "student");
            return "redirect:/usuarios";
        }

        if (type.equalsIgnoreCase("professor")) {
            Professor prof = new Professor();
            prof.setName(user.getName());
            prof.setEmail(user.getEmail());
            prof.setPassword(user.getPassword());
            userService.cadastrar(prof, "professor");
            return "redirect:/usuarios";
        }

        userService.cadastrar(user, "user");
        return "redirect:/usuarios";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<User> usuarios = userService.findAll();
        model.addAttribute("usuarios", usuarios);
        
        // Calcular contadores - CORRIGIDO para usar type
        long alunosCount = usuarios.stream()
            .filter(user -> "student".equalsIgnoreCase(user.getType()))
            .count();
        long professoresCount = usuarios.stream()
            .filter(user -> "professor".equalsIgnoreCase(user.getType()))
            .count();
        long outrosCount = usuarios.stream()
            .filter(user -> "user".equalsIgnoreCase(user.getType()) || user.getType() == null)
            .count();
            
        model.addAttribute("alunosCount", alunosCount);
        model.addAttribute("professoresCount", professoresCount);
        model.addAttribute("outrosCount", outrosCount);
        
        return "usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "editar";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:auth/login";
        }
        model.addAttribute("usuario", usuario);
        return "home";
    }

    // 🔹 Processar edição - CORRIGIDO para matrícula
    @PostMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, 
                           @ModelAttribute User user,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String matricula) {
    
        try {
            // Se for aluno e tiver matrícula, atualiza a matrícula
            if (user instanceof Student && matricula != null) {
                ((Student) user).setMatricula(matricula);
            }
            
            userService.update(id, user, password);
            return "redirect:/usuarios";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/usuarios?error=Erro ao atualizar usuário";
        }
    }

    // 🔹 Excluir usuário
    @GetMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/usuarios";
    }
        
    @GetMapping("/turmas")
    public String gerenciarTurmas(Model model) {
        model.addAttribute("turmas", classroomService.findAll());
        return "turmas";
    }

    // 🔹 Página de turmas do aluno - CORRIGIDO
@GetMapping("/aluno/{id}/turmas")
public String visualizarTurmasAluno(@PathVariable Long id, Model model) {
    Optional<User> userOpt = userService.findById(id);
    
    if (userOpt.isEmpty()) {
        return "redirect:/usuarios";
    }
    
    User user = userOpt.get();
    
    // Buscar as turmas do aluno
    List<Classroom> turmasAluno;
    try {
        turmasAluno = userService.getUserClassrooms(id);
    } catch (Exception e) {
        turmasAluno = List.of(); // Lista vazia em caso de erro
    }
    
    // Calcular estatísticas
    int totalTurmas = turmasAluno.size();
    int turmasAtivas = totalTurmas;
    int totalAlunosTurmas = calcularTotalAlunosTurmas(turmasAluno);
    double mediaAlunosPorTurma = calcularMediaAlunosPorTurma(turmasAluno);
    
    model.addAttribute("user", user);
    model.addAttribute("turmasAluno", turmasAluno);
    model.addAttribute("totalTurmas", totalTurmas);
    model.addAttribute("turmasAtivas", turmasAtivas);
    model.addAttribute("totalAlunosTurmas", totalAlunosTurmas);
    model.addAttribute("mediaAlunosPorTurma", mediaAlunosPorTurma);
    
    return "aluno-turmas";
}
    
    // 🔹 MÉTODOS AUXILIARES PARA ESTATÍSTICAS
    private int calcularTotalAlunosTurmas(List<Classroom> turmas) {
        int total = 0;
        for (Classroom turma : turmas) {
            try {
                total += classroomService.countUsersInClassroom(turma.getId());
            } catch (Exception e) {
                total += 0;
            }
        }
        return total;
    }
    
    private double calcularMediaAlunosPorTurma(List<Classroom> turmas) {
        if (turmas.isEmpty()) {
            return 0.0;
        }
        
        int totalAlunos = calcularTotalAlunosTurmas(turmas);
        double media = (double) totalAlunos / turmas.size();
        
        return Math.round(media * 10.0) / 10.0;
    }
}