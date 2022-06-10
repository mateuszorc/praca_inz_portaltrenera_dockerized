package com.example.portaltrenera.payload.request;

import com.example.portaltrenera.model.Comment;
import com.example.portaltrenera.model.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PostComment {

    private Long userId;
    private String title;
    private boolean trainingDone;
    private LocalDate trainingDay;
    private List<Comment> commentList;
    private Long id;
    private Long postId;
    private String content;


}
