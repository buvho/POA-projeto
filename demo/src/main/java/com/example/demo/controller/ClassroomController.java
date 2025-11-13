package com.example.demo.controller;

import com.example.demo.model.Classroom;
import com.example.demo.service.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Classrooms")
public class ClassroomController {
    private final ClassroomService service;
    public ClassroomController(ClassroomService service) { this.service = service; }

    @GetMapping
    public List<Classroom> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Classroom> create(@RequestBody Classroom Classroom) {
        Classroom saved = service.create(Classroom);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classroom> update(@PathVariable Long id, @RequestBody Classroom Classroom) {
        try {
            Classroom updated = service.update(id, Classroom);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
