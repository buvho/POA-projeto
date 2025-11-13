package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.PostType;
import com.example.demo.model.User;
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
        return repo.findAllOrderByPinnedAndDate();
    }

    public Optional<Post> findById(Long id) { 
        return repo.findById(id);
    }

    public List<Post> findByAuthor(Long authorId) {
        return repo.findByAuthorId(authorId);
    }

    public List<Post> findByType(PostType type) {
        return repo.findByType(type);
    }

    public List<Post> searchByContent(String keyword) {
        return repo.findByContentContainingIgnoreCase(keyword);
    }

    public List<Post> getPinnedPosts() {
        return repo.findByPinnedTrue();
    }

    public Post create(Post post) {
        return repo.save(post);
    }

    public Post createPost(String content, User author, PostType type) {
        Post post = new Post(content, author, type);
        return repo.save(post);
    }

    public Post update(Long id, Post updated) {
        return repo.findById(id).map(post -> {
            post.setContent(updated.getContent());
            post.setType(updated.getType());
            post.setPinned(updated.isPinned());
            return repo.save(post);
        }).orElseThrow(() -> new RuntimeException("Post não encontrado com id: " + id));
    }

    public Post togglePin(Long id) {
        return repo.findById(id).map(post -> {
            post.setPinned(!post.isPinned());
            return repo.save(post);
        }).orElseThrow(() -> new RuntimeException("Post não encontrado com id: " + id));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Post não encontrado com id: " + id);
        }
        repo.deleteById(id);
    }
}