package com.example.demo.service;

import com.example.demo.model.Professor;
import com.example.demo.repository.ProfessorRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository repo;

    public ProfessorService(ProfessorRepository repo) {
        this.repo = repo;
    }

    // 🔍 Lista todos os professores
    public List<Professor> findAll() {
        return repo.findAll();
    }

    // 🔍 Busca por ID
    public Optional<Professor> findById(Long id) {
        return repo.findById(id);
    }

    // 🧩 Criação de novo professor (com senha criptografada)
    public Professor create(Professor professor) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String raw = professor.getPassword(); // senha recebida do request
        String encoded = encoder.encode(raw); // criptografa
        professor.setPassword(encoded);       // substitui antes de salvar

        return repo.save(professor);
    }

    // ⚙️ Atualização de dados
    public Professor update(Long id, Professor updated) {
        return repo.findById(id).map(p -> {
            p.setName(updated.getName());
            p.setEmail(updated.getEmail());
            return repo.save(p);
        }).orElseThrow(() -> new RuntimeException("Professor não encontrado com id " + id));
    }

    // ❌ Exclusão
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
