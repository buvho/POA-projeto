package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public User cadastrar(User user) {
        // impede duplicidade de e-mail
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        // criptografa a senha
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 🔑 Login genérico
    public Optional<User> autenticar(String email, String senha) {
        Optional<User> usuarioOpt = userRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            if (encoder.matches(senha, usuario.getPassword())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }
}
