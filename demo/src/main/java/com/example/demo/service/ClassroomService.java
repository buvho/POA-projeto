package com.example.demo.service;

import com.example.demo.model.Classroom;
import com.example.demo.repository.ClassroomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {
    private final ClassroomRepository repo;
    
    public ClassroomService(ClassroomRepository repo) {
        this.repo = repo;
    }

    public List<Classroom> findAll() {
        return repo.findAll();
    }

    public Optional<Classroom> findById(Long id) { 
        return repo.findById(id);
    }

    public Classroom create(Classroom Classroom) {
        return repo.save(Classroom);
    }

    public Classroom update(Long id, Classroom updated) {
        return repo.findById(id).map(c -> {
            c.setName(updated.getName());
            return repo.save(c);
        }).orElseThrow(() -> new RuntimeException("Não foi encontrado o id " + id));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
