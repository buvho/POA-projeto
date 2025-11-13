package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime createdAt = LocalDateTime.now();

    // 🔹 Relação Many-to-Many com User
    @ManyToMany
    @JoinTable(
        name = "classroom_user",
        joinColumns = @JoinColumn(name = "classroom_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    // 🔹 Nova relação Many-to-Many com Post (lado inverso)
    @ManyToMany(mappedBy = "classrooms")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    // construtores
    public Classroom() {}
    public Classroom(String name) {
        this.name = name;
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // 🔹 Novos getters e setters para posts
    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
}