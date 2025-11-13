package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String senha) {
        Optional<User> usuario = userService.autenticar(email, senha);

        if (usuario.isPresent()) {
            User user = usuario.get();
            return "Login bem-sucedido! Bem-vindo, " + user.getName();
        } else {
            return "Credenciais inválidas.";
        }
    }
}
