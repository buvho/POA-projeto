package com.example.demo.controller;

import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Se já estiver logado, redirecionar para área apropriada
        User usuarioLogado = (User) session.getAttribute("usuarioLogado");
        if (usuarioLogado != null) {
            return redirecionarPorTipoUsuario(usuarioLogado);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpSession session,
                        Model model) {

        var usuario = userService.autenticar(email, senha);

        if (usuario.isPresent()) {
            session.setAttribute("usuarioLogado", usuario.get());
            return redirecionarPorTipoUsuario(usuario.get());
        } else {
            model.addAttribute("erro", "Credenciais inválidas.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    // Método auxiliar para redirecionamento
    private String redirecionarPorTipoUsuario(User usuario) {
        if (usuario instanceof Professor) {
            return "redirect:/home";
        } else if (usuario instanceof Student) {
            return "redirect:/aluno/feed";
        } else {
            // Usuário comum - redireciona para feed do aluno
            return "redirect:/aluno/feed";
        }
    }
}