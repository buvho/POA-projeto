package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ProfessorRepository professorRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 🔹 Cadastrar conforme o tipo
    public User cadastrar(User user, String type) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado!");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return switch (type.toLowerCase()) {
            case "student" -> studentRepository.save((Student) user);
            case "professor" -> professorRepository.save((Professor) user);
            default -> userRepository.save(user);
        };
    }

    // 🔑 Login genérico
    public Optional<User> autenticar(String email, String senha) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (encoder.matches(senha, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // 🔹 Listar todos
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // 🔹 Filtrar por tipo
    public List<User> findByType(String type) {
        return switch (type.toLowerCase()) {
            case "student" -> new ArrayList<>(studentRepository.findAll());
            case "professor" -> new ArrayList<>(professorRepository.findAll());
            default -> userRepository.findAll();
        };
    }

    // 🔹 Buscar por ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // 🔹 Deletar
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
