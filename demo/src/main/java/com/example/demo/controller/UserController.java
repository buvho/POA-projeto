package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Student;
import com.example.demo.model.Classroom;
import com.example.demo.model.Professor;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // 🔹 Listar todos
    @GetMapping
    public List<User> getAll(@RequestParam(required = false) String type) {
        if (type != null) return service.findByType(type);
        return service.findAll();
    }

    // 🔹 Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Criar novo (student, professor ou user) - CORRIGIDO
    @PostMapping
    public ResponseEntity<User> create(@RequestBody Map<String, Object> data, @RequestParam(defaultValue = "user") String type) {

        String name = (String) data.get("name");
        String email = (String) data.get("email");
        String password = (String) data.get("password");

        if (type.equalsIgnoreCase("student")) {
            Student student = new Student();
            student.setName(name);
            student.setEmail(email);
            student.setPassword(password);
            student.setMatricula((String) data.get("matricula"));
            User saved = service.cadastrar(student, "student");
            return ResponseEntity.ok(saved);
        }

        if (type.equalsIgnoreCase("professor")) {
            Professor prof = new Professor();
            prof.setName(name);
            prof.setEmail(email);
            prof.setPassword(password);
            User saved = service.cadastrar(prof, "professor");
            return ResponseEntity.ok(saved);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        User saved = service.cadastrar(user, "user");

        return ResponseEntity.ok(saved);
    }

    // 🔹 Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

     // 🔹 Conectar usuário a uma classroom
    @PostMapping("/{userId}/classrooms/{classroomId}")
    public ResponseEntity<User> connectToClassroom(
            @PathVariable Long userId, 
            @PathVariable Long classroomId) {
        try {
            User user = service.connectToClassroom(userId, classroomId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Listar classrooms do usuário - CORRIGIDO com melhor tratamento de erro
    @GetMapping("/{userId}/classrooms")
    public ResponseEntity<List<Classroom>> getUserClassrooms(@PathVariable Long userId) {
        try {
            List<Classroom> classrooms = service.getUserClassrooms(userId);
            return ResponseEntity.ok(classrooms);
        } catch (RuntimeException e) {
            // Retorna lista vazia em vez de 404 para facilitar o frontend
            return ResponseEntity.ok(List.of());
        }
    }

    // 🔹 Desconectar usuário de uma classroom
    @DeleteMapping("/{userId}/classrooms/{classroomId}")
    public ResponseEntity<Void> disconnectFromClassroom(
            @PathVariable Long userId, 
            @PathVariable Long classroomId) {
        try {
            service.disconnectFromClassroom(userId, classroomId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Verificar se usuário está em uma classroom
    @GetMapping("/{userId}/classrooms/{classroomId}/is-member")
    public ResponseEntity<Boolean> isUserInClassroom(
            @PathVariable Long userId, 
            @PathVariable Long classroomId) {
        boolean isMember = service.isUserInClassroom(userId, classroomId);
        return ResponseEntity.ok(isMember);
    }
    
}