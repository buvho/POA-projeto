package com.example.demo.dto;

import com.example.demo.model.User;

public class UserResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String tipo;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.nome = user.getName();
        this.email = user.getEmail();
        this.tipo = user.getClass().getSimpleName(); // Aluno ou Professor
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTipo() { return tipo; }
}
