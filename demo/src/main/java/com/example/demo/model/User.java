package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
        name = "user_classroom",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "classroom_id")
    )
    @JsonIgnore
    private List<Classroom> classrooms = new ArrayList<>();

    // construtores
    public User() {}
    public User(String name, String email, String password) {
        this.name = name; this.email = email; this.password = password;
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getType() {
        if (this instanceof Student) return "Aluno";
        if (this instanceof Professor) return "Professor";
        return "Usuário";
    }

    public List<Classroom> getClassrooms() { return classrooms; }
    public void setClassrooms(List<Classroom> classrooms) { this.classrooms = classrooms; }

    public void addClassroom(Classroom classroom) {
        if (this.classrooms == null) {
            this.classrooms = new ArrayList<>();
        }
        if (!this.classrooms.contains(classroom)) {
            this.classrooms.add(classroom);
            classroom.getUsers().add(this);
        }
    }

    public void removeClassroom(Classroom classroom) {
        if (this.classrooms != null) {
            this.classrooms.remove(classroom);
            classroom.getUsers().remove(this);
        }
    }

    public Optional<Classroom> findClassroomById(Long classroomId) {
        return this.classrooms.stream()
                .filter(c -> c.getId().equals(classroomId))
                .findFirst();
    }
}