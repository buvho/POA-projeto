package com.example.demo.controller;

import com.example.demo.model.Classroom;
import com.example.demo.model.User;
import com.example.demo.service.ClassroomService;
import com.example.demo.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {
    private final ClassroomService classroomService;
    private final UserService userService;

    public ClassroomController(ClassroomService classroomService, UserService userService) {
        this.classroomService = classroomService;
        this.userService = userService;
    }

    @GetMapping
    public List<Classroom> getAll() { 
        return classroomService.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getById(@PathVariable Long id) {
        return classroomService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Buscar classrooms por nome
    @GetMapping("/search")
    public List<Classroom> searchByName(@RequestParam String name) {
        return classroomService.findByNameContaining(name);
    }

    // 🔹 Buscar classrooms disponíveis para um usuário
    @GetMapping("/available/{userId}")
    public ResponseEntity<List<Classroom>> getAvailableForUser(@PathVariable Long userId) {
        try {
            List<Classroom> availableClassrooms = classroomService.findAvailableForUser(userId);
            return ResponseEntity.ok(availableClassrooms);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Contar usuários em uma classroom
    @GetMapping("/{classroomId}/user-count")
    public ResponseEntity<Integer> getUserCount(@PathVariable Long classroomId) {
        try {
            int userCount = classroomService.countUsersInClassroom(classroomId);
            return ResponseEntity.ok(userCount);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(0);
        }
    }

    // 🔹 Verificar se classroom existe
    @GetMapping("/{classroomId}/exists")
    public ResponseEntity<Boolean> classroomExists(@PathVariable Long classroomId) {
        boolean exists = classroomService.existsById(classroomId);
        return ResponseEntity.ok(exists);
    }

    // 🔹 Listar usuários de uma classroom
    @GetMapping("/{classroomId}/users")
    public ResponseEntity<List<User>> getClassroomUsers(@PathVariable Long classroomId) {
        try {
            List<User> users = userService.getClassroomUsers(classroomId);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody Classroom classroom) {
        // 🔹 Validação opcional: verificar se já existe classroom com mesmo nome
        Classroom saved = classroomService.create(classroom);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classroom> update(@PathVariable Long id, @RequestBody Classroom classroom) {
        try {
            Classroom updated = classroomService.update(id, classroom);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classroomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}