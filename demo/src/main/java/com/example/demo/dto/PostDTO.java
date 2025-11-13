package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.model.Post;
import com.example.demo.model.PostType;

// CREATE: PostDTO.java
public class PostDTO {
    private Long id;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private PostType type;
    private boolean pinned;
    
    // constructor, getters, setters
    public PostDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.authorName = post.getAuthor().getName();
        this.createdAt = post.getCreatedAt();
        this.type = post.getType();
        this.pinned = post.isPinned();
    }
}