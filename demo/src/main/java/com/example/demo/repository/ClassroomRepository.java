package com.example.demo.repository;

import com.example.demo.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    
    // 🔹 Buscar classrooms por nome (ignorando case)
    List<Classroom> findByNameContainingIgnoreCase(String name);
    
    // 🔹 Verificar se existe classroom com determinado nome
    boolean existsByName(String name);
}