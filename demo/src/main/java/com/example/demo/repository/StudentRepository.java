package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // aqui você pode adicionar consultas customizadas: findByEmail, etc.

    // find + By + Email = SELECT * FROM WHERE email = ?
    Optional<Student> findByEmail(String email);
}
