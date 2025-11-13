package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Student;
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

    // 🔹 Criar novo (student, professor ou user)
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
        return ResponseEntity.ok(service.cadastrar(student, "student"));
    }

    if (type.equalsIgnoreCase("professor")) {
        Professor prof = new Professor();
        prof.setName(name);
        prof.setEmail(email);
        prof.setPassword(password);
        return ResponseEntity.ok(service.cadastrar(prof, "professor"));
    }

    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(password);

    return ResponseEntity.ok(service.cadastrar(user, "user"));
}
    // 🔹 Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
