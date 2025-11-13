package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Professor extends User {

    // construtores
    public Professor() {}

    public Professor(String name, String email, String password) {
        super(name,email,password);
    }
}
