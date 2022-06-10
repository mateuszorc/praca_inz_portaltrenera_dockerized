package com.example.portaltrenera.dto;

import com.example.portaltrenera.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserPostDtoMapper {

    public static UserPostDto MapToUserPostDto(User user) {
        return UserPostDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .postList(user.getPostList())
                .build();
    }
}
