package com.example.portaltrenera.dto;

import com.example.portaltrenera.model.Post;
import com.example.portaltrenera.model.UserRole;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

@Getter
@Builder
public class UserPostDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private List<Post> postList;
}


