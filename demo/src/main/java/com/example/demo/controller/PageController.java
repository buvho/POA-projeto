package com.example.demo.controller;

import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    private final UserService service;

    public PageController(UserService service) {
        this.service = service;
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
            service.cadastrar(student, "student");
            return "redirect:/usuarios";
        }

        if (type.equalsIgnoreCase("professor")) {
            Professor prof = new Professor();
            prof.setName(user.getName());
            prof.setEmail(user.getEmail());
            prof.setPassword(user.getPassword());
            service.cadastrar(prof, "professor");
            return "redirect:/usuarios";
        }

        service.cadastrar(user, "user");
        return "redirect:/usuarios";
    }

    private String handleCadastro(User user, String type) {
    try {
        service.cadastrar(user, type);
        return "redirect:/usuarios"; // sucesso
    } catch (Exception e) {
        e.printStackTrace();
        return "error"; // ou uma página de erro
    }
}
    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", service.findAll());
        return "usuarios";
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

}
