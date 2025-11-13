package com.example.demo.repository;

import com.example.demo.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // aqui você pode adicionar consultas customizadas: findByEmail, etc.

    // find + By + Email = SELECT * FROM WHERE email = ?
    Optional<Aluno> findByEmail(String email);
}
