package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Encontrar posts por autor
    List<Post> findByAuthorId(Long authorId);
    
    // Encontrar posts por tipo
    List<Post> findByType(PostType type);
    
    // Encontrar posts fixados
    List<Post> findByPinnedTrue();
    
    // Encontrar posts mais recentes primeiro
    List<Post> findAllByOrderByCreatedAtDesc();
    
    // Buscar posts por conteúdo (like)
    List<Post> findByContentContainingIgnoreCase(String keyword);
    
    // Posts fixados no topo, depois os mais recentes
    @Query("SELECT p FROM Post p ORDER BY p.pinned DESC, p.createdAt DESC")
    List<Post> findAllOrderByPinnedAndDate();
}