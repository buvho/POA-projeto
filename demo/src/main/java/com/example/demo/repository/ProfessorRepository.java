package com.example.demo.repository;

import com.example.demo.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    // aqui você pode adicionar consultas customizadas: findByEmail, etc.

    // find + By + Email = SELECT * FROM WHERE email = ?
    Optional<Professor> findByEmail(String email);
}
