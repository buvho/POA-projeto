package com.example.demo.controller;

import com.example.demo.model.Classroom;
import com.example.demo.model.Post;
import com.example.demo.model.PostType;
import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.service.ClassroomService;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    private final UserService userService;
    private final ClassroomService classroomService;
    private final PostService postService;
    
    public PageController(UserService userService, ClassroomService classroomService, PostService postService) {
        this.userService = userService;
        this.classroomService = classroomService;
        this.postService = postService;
    }

    // ===== PÁGINA DE CADASTRO =====
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

    // ===== PÁGINA DE USUÁRIOS (APENAS PROFESSORES) =====
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        List<User> usuarios = userService.findAll();
        model.addAttribute("usuarios", usuarios);
        
        // Calcular contadores
        long alunosCount = usuarios.stream()
            .filter(user -> user instanceof Student)
            .count();
        long professoresCount = usuarios.stream()
            .filter(user -> user instanceof Professor)
            .count();
        long outrosCount = usuarios.stream()
            .filter(user -> !(user instanceof Student) && !(user instanceof Professor))
            .count();
            
        model.addAttribute("alunosCount", alunosCount);
        model.addAttribute("professoresCount", professoresCount);
        model.addAttribute("outrosCount", outrosCount);
        
        return "usuarios";
    }

    // ===== EDIÇÃO DE USUÁRIOS =====
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "editar";
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, 
                           @ModelAttribute User user,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) String matricula,
                           HttpSession session) {
    
        User usuarioLogado = (User) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuarioLogado instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        try {
            // Busca o usuário existente
            Optional<User> existingUserOpt = userService.findById(id);
            if (existingUserOpt.isEmpty()) {
                return "redirect:/usuarios?error=Usuário não encontrado";
            }
            
            User existingUser = existingUserOpt.get();
            
            // Atualiza campos básicos
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            
            // Atualiza senha se fornecida
            if (password != null && !password.trim().isEmpty()) {
                existingUser.setPassword(password);
            }
            
            // Atualiza matrícula para Student
            if (existingUser instanceof Student && matricula != null) {
                ((Student) existingUser).setMatricula(matricula);
            }
            
            userService.update(existingUser);
            return "redirect:/usuarios";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/usuarios?error=Erro ao atualizar usuário";
        }
    }

    // ===== EXCLUSÃO DE USUÁRIOS =====
    @GetMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        userService.delete(id);
        return "redirect:/usuarios";
    }

    // ===== HOME (APENAS PROFESSORES) =====
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor - se for aluno, redirecionar
        if (usuario instanceof Student) {
            return "redirect:/aluno/feed";
        }
        
        // Se não for professor nem aluno, também redirecionar
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed";
        }
        
        List<User> usuarios = userService.findAll();
        List<Post> posts = postService.findAll();
        
        // Calcular contadores
        long alunosCount = usuarios.stream()
            .filter(user -> user instanceof Student)
            .count();
        long professoresCount = usuarios.stream()
            .filter(user -> user instanceof Professor)
            .count();
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("alunosCount", alunosCount);
        model.addAttribute("professoresCount", professoresCount);
        model.addAttribute("postsCount", posts.size());
        model.addAttribute("recentPosts", posts.stream().limit(4).collect(Collectors.toList()));
        
        return "home";
    }

    // ===== PÁGINA DE TURMAS =====
    @GetMapping("/turmas")
    public String gerenciarTurmas(Model model, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        model.addAttribute("turmas", classroomService.findAll());
        return "turmas";
    }

    // ===== PÁGINA DE POSTS (APENAS PROFESSORES) =====
    @GetMapping("/posts")
    public String postsPage(Model model, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        List<Post> posts = postService.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("usuario", usuario);
        model.addAttribute("postTypes", PostType.values());
        model.addAttribute("newPost", new Post());
        
        return "posts";
    }

    @PostMapping("/posts")
    public String criarPost(@ModelAttribute("newPost") Post post,
                           @RequestParam String content,
                           @RequestParam PostType type,
                           HttpSession session) {
        User autor = (User) session.getAttribute("usuarioLogado");
        if (autor == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(autor instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        post.setContent(content);
        post.setType(type);
        post.setAuthor(autor);
        
        postService.create(post);
        return "redirect:/posts?success=Post publicado com sucesso!";
    }

    @PostMapping("/posts/{id}/pin")
    public String toggleFixarPost(@PathVariable Long id, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        try {
            postService.togglePin(id);
            return "redirect:/posts";
        } catch (RuntimeException e) {
            return "redirect:/posts?error=Post não encontrado";
        }
    }

    @PostMapping("/posts/{id}/delete")
    public String excluirPost(@PathVariable Long id, HttpSession session) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é professor
        if (!(usuario instanceof Professor)) {
            return "redirect:/aluno/feed?error=Acesso restrito a professores";
        }
        
        try {
            Optional<Post> post = postService.findById(id);
            
            // Verifica se o usuário tem permissão para excluir
            if (post.isPresent() && 
                (usuario.getId().equals(post.get().getAuthor().getId()) || 
                 usuario instanceof Professor)) {
                postService.delete(id);
                return "redirect:/posts";
            } else {
                return "redirect:/posts?error=Sem permissão para excluir este post";
            }
        } catch (RuntimeException e) {
            return "redirect:/posts?error=Erro ao excluir post";
        }
    }

    // ===== ÁREA DO ALUNO (ALUNOS E PROFESSORES) =====
    @GetMapping("/aluno/feed")
    public String alunoFeed(Model model, HttpSession session,
                           @RequestParam(required = false) String turma,
                           @RequestParam(required = false) Boolean apenasProfessores,
                           @RequestParam(required = false) String search) {
        User usuario = (User) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é aluno ou professor - PERMITIR AMBOS
        if (!(usuario instanceof Student) && !(usuario instanceof Professor)) {
            return "redirect:/home?error=Acesso permitido apenas para alunos e professores";
        }
        
        List<Post> todosPosts = postService.findAll();
        List<Post> postsFiltrados = new ArrayList<>(todosPosts);
        
        // Aplicar filtros
        if (apenasProfessores != null && apenasProfessores) {
            postsFiltrados = postsFiltrados.stream()
                .filter(post -> post.getAuthor() instanceof Professor)
                .collect(Collectors.toList());
        }
        
        if (search != null && !search.trim().isEmpty()) {
            postsFiltrados = postsFiltrados.stream()
                .filter(post -> post.getContent().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        model.addAttribute("posts", postsFiltrados);
        model.addAttribute("aluno", usuario);
        model.addAttribute("turmaFiltro", turma);
        model.addAttribute("apenasProfessores", apenasProfessores);
        model.addAttribute("searchTerm", search);
        model.addAttribute("postTypes", PostType.values());
        
        // Estatísticas para o dashboard
        long totalPosts = todosPosts.size();
        long postsProfessores = todosPosts.stream()
            .filter(post -> post.getAuthor() instanceof Professor)
            .count();
        long postsImportantes = todosPosts.stream()
            .filter(post -> post.getType() == PostType.IMPORTANT)
            .count();
        
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("postsProfessores", postsProfessores);
        model.addAttribute("postsImportantes", postsImportantes);
        
        return "aluno-feed";
    }

    @PostMapping("/aluno/feed/post")
    public String criarPostAluno(@RequestParam String content,
                                @RequestParam PostType type,
                                HttpSession session) {
        User autor = (User) session.getAttribute("usuarioLogado");
        if (autor == null) {
            return "redirect:/auth/login";
        }
        
        // Verificar se é aluno ou professor
        if (!(autor instanceof Student) && !(autor instanceof Professor)) {
            return "redirect:/home?error=Acesso permitido apenas para alunos e professores";
        }
        
        // Criar o post
        Post post = new Post();
        post.setContent(content);
        post.setType(type);
        post.setAuthor(autor);
        
        postService.create(post);
        return "redirect:/aluno/feed?success=Post publicado com sucesso!";
    }
}