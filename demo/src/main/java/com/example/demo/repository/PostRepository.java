package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Métodos existentes...
    List<Post> findByAuthorId(Long authorId);
    List<Post> findByType(PostType type);
    List<Post> findByContentContainingIgnoreCase(String keyword);
    List<Post> findByPinnedTrue();
    
    @Query("SELECT p FROM Post p ORDER BY p.pinned DESC, p.createdAt DESC")
    List<Post> findAllOrderByPinnedAndDate();

    // 🔹 Novos métodos para buscar posts por classroom
    @Query("SELECT p FROM Post p JOIN p.classrooms c WHERE c.id = :classroomId ORDER BY p.pinned DESC, p.createdAt DESC")
    List<Post> findByClassroomId(@Param("classroomId") Long classroomId);

    @Query("SELECT p FROM Post p JOIN p.classrooms c WHERE c.id = :classroomId AND p.pinned = true")
    List<Post> findPinnedPostsByClassroomId(@Param("classroomId") Long classroomId);

    @Query("SELECT p FROM Post p JOIN p.classrooms c WHERE c.id = :classroomId AND p.type = :type")
    List<Post> findByClassroomIdAndType(@Param("classroomId") Long classroomId, @Param("type") PostType type);
    
    @Query("SELECT DISTINCT p FROM Post p JOIN p.classrooms c JOIN c.users u WHERE u.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findByUserClassrooms(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.classrooms c WHERE c.id IN :classroomIds ORDER BY p.pinned DESC, p.createdAt DESC")
    List<Post> findByClassroomsIdIn(@Param("classroomIds") List<Long> classroomIds);
    
    // 🔹 MÉTODO ALTERNATIVO - Busca posts pelas turmas do usuário (MAIS EFICIENTE)
    // NO PostRepository.java - MELHORAR A QUERY
    @Query("SELECT DISTINCT p FROM Post p " +
        "JOIN FETCH p.author " +
        "JOIN p.classrooms c " +
        "JOIN c.users u " +
        "WHERE u.id = :userId " +
        "ORDER BY p.pinned DESC, p.createdAt DESC")
    List<Post> findPostsByUserClassrooms(@Param("userId") Long userId);
}
