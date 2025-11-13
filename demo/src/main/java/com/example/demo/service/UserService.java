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
    private ClassroomRepository classroomRepository;
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

    // 🔹 SALVAR usuário (método novo - importante para edição)
    public User save(User user) {
        // Se a senha não está codificada (quando vem do formulário de edição)
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // 🔹 ATUALIZAR usuário (método novo - para edição)
    public User update(Long id, User userDetails, String newPassword) {
        return userRepository.findById(id)
            .map(user -> {
                user.setName(userDetails.getName());
                user.setEmail(userDetails.getEmail());
                
                // Atualiza senha apenas se for fornecida uma nova
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    user.setPassword(encoder.encode(newPassword));
                }
                
                // Se for Student, atualiza a matrícula
                if (user instanceof Student && userDetails instanceof Student) {
                    ((Student) user).setMatricula(((Student) userDetails).getMatricula());
                }
                
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));
    }

    // 🔹 Buscar por email (método auxiliar para validações)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

        public User connectToClassroom(Long userId, Long classroomId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));
        
        Classroom classroom = classroomRepository.findById(classroomId)
            .orElseThrow(() -> new RuntimeException("Classroom não encontrada com ID: " + classroomId));
        
        user.addClassroom(classroom);
        return userRepository.save(user);
    }

    // 🔹 Desconectar usuário de uma classroom
    public void disconnectFromClassroom(Long userId, Long classroomId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));
        
        Classroom classroom = user.findClassroomById(classroomId)
            .orElseThrow(() -> new RuntimeException("Classroom não encontrada para este usuário: " + classroomId));
        
        user.removeClassroom(classroom);
        userRepository.save(user);
    }

    // 🔹 Listar classrooms do usuário
    public List<Classroom> getUserClassrooms(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));
        return user.getClassrooms();
    }

    // 🔹 Listar usuários de uma classroom
    public List<User> getClassroomUsers(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
            .orElseThrow(() -> new RuntimeException("Classroom não encontrada com ID: " + classroomId));
        return classroom.getUsers();
    }

    // 🔹 Verificar se usuário está em uma classroom
    public boolean isUserInClassroom(Long userId, Long classroomId) {
        return userRepository.findById(userId)
            .map(user -> user.getClassrooms().stream()
                .anyMatch(classroom -> classroom.getId().equals(classroomId)))
            .orElse(false);
    }
}

