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

    private String handleCadastro(User user, String type) {
    try {
        userService.cadastrar(user, type);
        return "redirect:/usuarios"; // sucesso
    } catch (Exception e) {
        e.printStackTrace();
        return "error"; // ou uma página de erro
    }
}
     @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<User> usuarios = userService.findAll();
        model.addAttribute("usuarios", usuarios);
        
        // Calcular contadores
        long alunosCount = usuarios.stream()
            .filter(user -> user.getClass().getSimpleName().equals("Student"))
            .count();
        long professoresCount = usuarios.stream()
            .filter(user -> user.getClass().getSimpleName().equals("Professor"))
            .count();
        long outrosCount = usuarios.stream()
            .filter(user -> user.getClass().getSimpleName().equals("User"))
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


    // 🔹 Processar edição
  @PostMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, 
                           @ModelAttribute User user,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String matricula) {
    
    try {
        // Usa o método update do userService
        userService.update(id, user, password);
        return "redirect:/usuarios";
    } catch (Exception e) {
        // Log do erro (em produção use um logger)
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


}
