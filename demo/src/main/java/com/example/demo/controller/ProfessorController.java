package com.example.demo.controller;

import com.example.demo.model.Professor;
import com.example.demo.service.ProfessorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {
    private final ProfessorService service;
    public ProfessorController(ProfessorService service) { this.service = service; }

    @GetMapping
    public List<Professor> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Professor> create(@RequestBody Professor professor) {
        Professor saved = service.create(professor);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> update(@PathVariable Long id, @RequestBody Professor professor) {
        try {
            Professor updated = service.update(id, professor);
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
