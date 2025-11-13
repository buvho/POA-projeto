package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private PostType type = PostType.GENERAL;

    private boolean pinned = false;

    // 🔹 Nova relação Many-to-Many com Classroom
    @ManyToMany
    @JoinTable(
        name = "post_classrooms",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "classroom_id")
    )
    @JsonIgnore
    private List<Classroom> classrooms = new ArrayList<>();

    // construtores
    public Post() {}
    
    public Post(String content, User author) {
        this.content = content; 
        this.author = author;
    }

    public Post(String content, User author, PostType type) {
        this.content = content; 
        this.author = author;
        this.type = type;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }

    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }

    // 🔹 Novos getters e setters para classrooms
    public List<Classroom> getClassrooms() { return classrooms; }
    public void setClassrooms(List<Classroom> classrooms) { this.classrooms = classrooms; }

    // 🔹 Métodos utilitários para manipulação da lista
    public void addClassroom(Classroom classroom) {
        this.classrooms.add(classroom);
        classroom.getPosts().add(this);
    }

    public void removeClassroom(Classroom classroom) {
        this.classrooms.remove(classroom);
        classroom.getPosts().remove(this);
    }
}