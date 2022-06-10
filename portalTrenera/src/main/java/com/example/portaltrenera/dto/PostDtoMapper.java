package com.example.portaltrenera.dto;

import com.example.portaltrenera.model.Post;
import com.example.portaltrenera.payload.request.PostComment;

import java.util.List;
import java.util.stream.Collectors;

public class PostDtoMapper {

    public static List<PostDto> MapToPostDtos(List<Post> posts) {
        return posts.stream()
                .map(PostDtoMapper::MapToPostDto)
                .collect(Collectors.toList());
    }

    public static PostDto MapToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .trainingDone(post.isTrainingDone())
                .trainingDay(post.getTrainingDay())
                .build();
    }
}
