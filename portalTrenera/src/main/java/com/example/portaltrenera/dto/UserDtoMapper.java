package com.example.portaltrenera.dto;

import com.example.portaltrenera.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDtoMapper {
    public static List<UserDto> MapToUserDtos(List<User> users) {
        return users.stream()
                .map(UserDtoMapper::MapToUserDto)
                .collect(Collectors.toList());
    }

    public static UserDto MapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();

    }
}
