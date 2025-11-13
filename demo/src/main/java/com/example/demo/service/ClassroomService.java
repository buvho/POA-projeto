package com.example.demo.service;

import com.example.demo.model.Classroom;
import com.example.demo.model.User;
import com.example.demo.repository.ClassroomRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {
    private final ClassroomRepository classroomRepo;
    private final UserService userService; // 🔹 Injeção do UserService
    
    // 🔹 Construtor com injeção de dependências
    public ClassroomService(ClassroomRepository classroomRepo, UserService userService) {
        this.classroomRepo = classroomRepo;
        this.userService = userService;
    }

    public List<Classroom> findAll() {
        return classroomRepo.findAll();
    }

    public Optional<Classroom> findById(Long id) { 
        return classroomRepo.findById(id);
    }

    public Classroom create(Classroom classroom) {
        return classroomRepo.save(classroom);
    }

    public Classroom update(Long id, Classroom updated) {
        return classroomRepo.findById(id).map(classroom -> {
            classroom.setName(updated.getName());
            return classroomRepo.save(classroom);
        }).orElseThrow(() -> new RuntimeException("Classroom não encontrada com ID: " + id));
    }

    public void delete(Long id) {
        classroomRepo.deleteById(id);
    }

    // 🔹 Buscar classrooms disponíveis (não conectadas a um usuário específico)
    public List<Classroom> findAvailableForUser(Long userId) {
        List<Classroom> allClassrooms = classroomRepo.findAll();
        
        // Usa o UserService para pegar as classrooms do usuário
        List<Classroom> userClassrooms = userService.getUserClassrooms(userId);
        
        // Filtra as classrooms que o usuário NÃO está conectado
        allClassrooms.removeAll(userClassrooms);
        return allClassrooms;
    }

    // 🔹 Buscar classrooms por nome (método adicional útil)
    public List<Classroom> findByNameContaining(String name) {
        return classroomRepo.findByNameContainingIgnoreCase(name);
    }

    // 🔹 Contar quantos usuários estão em uma classroom
    public int countUsersInClassroom(Long classroomId) {
        try {
            List<User> users = userService.getClassroomUsers(classroomId);
            return users.size();
        } catch (RuntimeException e) {
            return 0;
        }
    }

    // 🔹 Verificar se classroom existe
    public boolean existsById(Long classroomId) {
        return classroomRepo.existsById(classroomId);
    }
}