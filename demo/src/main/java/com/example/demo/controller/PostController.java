package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.PostType;
import com.example.demo.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService service;
    
    public PostController(PostService service) { 
        this.service = service; 
    }

    @GetMapping
    public List<Post> getAll() { 
        return service.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/author/{authorId}")
    public List<Post> getByAuthor(@PathVariable Long authorId) {
        return service.findByAuthor(authorId);
    }

    @GetMapping("/type/{type}")
    public List<Post> getByType(@PathVariable PostType type) {
        return service.findByType(type);
    }

    @GetMapping("/search")
    public List<Post> search(@RequestParam String keyword) {
        return service.searchByContent(keyword);
    }

    @GetMapping("/pinned")
    public List<Post> getPinnedPosts() {
        return service.getPinnedPosts();
    }

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        Post saved = service.create(post);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post post) {
        try {
            Post updated = service.update(id, post);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<Post> togglePin(@PathVariable Long id) {
        try {
            Post updated = service.togglePin(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}