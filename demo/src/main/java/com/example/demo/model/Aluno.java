package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Aluno extends User {
    private String matricula;

    // construtores
    public Aluno() {}
    
    public Aluno(String name, String email, String matricula, String password) {
        super(name, email, password);
        this.matricula = matricula;
    }

    // getters e setters
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
