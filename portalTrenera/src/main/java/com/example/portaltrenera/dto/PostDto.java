package com.example.portaltrenera.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


@Builder
@Getter
public class PostDto {

    private Long id;
    private Long userId;
    private String title;
    private boolean trainingDone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate trainingDay;
}
