package com.example.portaltrenera.repository;

import com.example.portaltrenera.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdIn(List<Long> postIds);

    Comment findByPostIdAndId(Long postId, Long id);

    void deleteByPostIdAndId(Long postId, Long id);
}
