package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository repo;
    
    public PostService(PostRepository repo) {
        this.repo = repo;
    }

    public List<Post> findAll() {
        return repo.findAll();
    }

    public Optional<Post> findById(Long id) { 
        return repo.findById(id);
    }

    public Post create(Post Post) {
        return repo.save(Post);
    }

    public Post update(Long id, Post updated) {
        return repo.findById(id).map(c -> {
            c.setContent(updated.getContent());
            return repo.save(c);
        }).orElseThrow(() -> new RuntimeException("Não foi encontrado o id " + id));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
