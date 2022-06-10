package com.example.portaltrenera.repository;

import com.example.portaltrenera.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p")
    List<Post> findAllPosts();

    List<Post> findAllByUserId(Long userId);

    Post findByUserIdAndId(Long userId, Long postId);
}
