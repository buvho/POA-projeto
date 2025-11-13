package com.example.demo.service;

import com.example.demo.model.Aluno;
import com.example.demo.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository repo;

    public AlunoService(AlunoRepository repo) {
        this.repo = repo;
    }

    // 🔍 Lista todos os alunos
    public List<Aluno> findAll() {
        return repo.findAll();
    }

    // 🔍 Busca por ID
    public Optional<Aluno> findById(Long id) {
        return repo.findById(id);
    }

    // 🧩 Criação de novo aluno
    public Aluno create(Aluno aluno) {
        return repo.save(aluno);
    }

    // ⚙️ Atualização
    public Aluno update(Long id, Aluno updated) {
        return repo.findById(id).map(a -> {
            a.setName(updated.getName());
            a.setEmail(updated.getEmail());
            a.setMatricula(updated.getMatricula());
            return repo.save(a);
        }).orElseThrow(() -> new RuntimeException("Aluno não encontrado com id " + id));
    }

    // ❌ Exclusão
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
