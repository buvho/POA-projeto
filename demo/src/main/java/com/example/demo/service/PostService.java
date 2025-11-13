package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.PostType;
import com.example.demo.model.User;
import com.example.demo.model.Classroom;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.ClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository repo;
    private final ClassroomRepository classroomRepository;
    
    public PostService(PostRepository repo, ClassroomRepository classroomRepository) {
        this.repo = repo;
        this.classroomRepository = classroomRepository;
    }

    // Métodos existentes...
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

    // 🔹 NOVOS MÉTODOS PARA MANIPULAR A RELAÇÃO COM CLASSROOMS

    public List<Post> findByClassroom(Long classroomId) {
        return repo.findByClassroomId(classroomId);
    }

    public List<Post> findPinnedPostsByClassroom(Long classroomId) {
        return repo.findPinnedPostsByClassroomId(classroomId);
    }

    public List<Post> findByClassroomAndType(Long classroomId, PostType type) {
        return repo.findByClassroomIdAndType(classroomId, type);
    }
    

    @Transactional
    public Post addClassroomToPost(Long postId, Long classroomId) {
        Post post = repo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado com id: " + postId));
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom não encontrado com id: " + classroomId));
        
        post.addClassroom(classroom);
        return repo.save(post);
    }

    @Transactional
    public Post removeClassroomFromPost(Long postId, Long classroomId) {
        Post post = repo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado com id: " + postId));
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom não encontrado com id: " + classroomId));
        
        post.removeClassroom(classroom);
        return repo.save(post);
    }

    @Transactional
    public Post updatePostClassrooms(Long postId, List<Long> classroomIds) {
        Post post = repo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado com id: " + postId));
        
        // Limpa classrooms atuais
        post.getClassrooms().clear();
        
        // Adiciona novos classrooms
        for (Long classroomId : classroomIds) {
            Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new RuntimeException("Classroom não encontrado com id: " + classroomId));
            post.addClassroom(classroom);
        }
        
        return repo.save(post);
    }

    public List<Post> findByClassrooms(List<Long> classroomIds) {
        if (classroomIds == null || classroomIds.isEmpty()) {
            return List.of();
        }
        return repo.findByClassroomsIdIn(classroomIds);
    }

    @Transactional(readOnly = true)
    public List<Post> findPostsByUserClassrooms(Long userId) {
        return repo.findPostsByUserClassrooms(userId);
}

    
}