package com.example.demo.controller;

import com.example.demo.dto.PostDTO;
import com.example.demo.model.Post;
import com.example.demo.model.PostType;
import com.example.demo.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService service;
    
    public PostController(PostService service) { 
        this.service = service; 
    }

    // Métodos existentes...
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

    // 🔹 NOVOS ENDPOINTS PARA MANIPULAR A RELAÇÃO COM CLASSROOMS

    @GetMapping("/classroom/{classroomId}")
    public List<Post> getByClassroom(@PathVariable Long classroomId) {
        return service.findByClassroom(classroomId);
    }

    @GetMapping("/classroom/{classroomId}/pinned")
    public List<Post> getPinnedPostsByClassroom(@PathVariable Long classroomId) {
        return service.findPinnedPostsByClassroom(classroomId);
    }

    @GetMapping("/classroom/{classroomId}/type/{type}")
    public List<Post> getByClassroomAndType(@PathVariable Long classroomId, @PathVariable PostType type) {
        return service.findByClassroomAndType(classroomId, type);
    }

    @PostMapping("/{postId}/classroom/{classroomId}")
    public ResponseEntity<Post> addClassroomToPost(@PathVariable Long postId, @PathVariable Long classroomId) {
        try {
            Post updated = service.addClassroomToPost(postId, classroomId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{postId}/classroom/{classroomId}")
    public ResponseEntity<Post> removeClassroomFromPost(@PathVariable Long postId, @PathVariable Long classroomId) {
        try {
            Post updated = service.removeClassroomFromPost(postId, classroomId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{postId}/classrooms")
    public ResponseEntity<Post> updatePostClassrooms(@PathVariable Long postId, @RequestBody List<Long> classroomIds) {
        try {
            Post updated = service.updatePostClassrooms(postId, classroomIds);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // NO PostController.java - ADICIONAR ESTE ENDPOINT
    @GetMapping("/my-classrooms")
    public ResponseEntity<List<Post>> getPostsByMyClassrooms(@RequestParam Long userId) {
        try {
            List<Post> posts = service.findPostsByUserClassrooms(userId);
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
        // NO PostController.java
    @GetMapping("/user/{userId}/feed")
    public ResponseEntity<List<PostDTO>> getUserFeed(@PathVariable Long userId) {
        try {
            List<Post> posts = service.findPostsByUserClassrooms(userId);
            List<PostDTO> postDTOs = posts.stream()
                    .map(PostDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(postDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}