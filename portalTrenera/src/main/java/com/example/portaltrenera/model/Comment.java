package com.example.portaltrenera.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {

    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sequence"
    )
    private Long id;
    private Long postId;
    @Lob
    private String content;

    @Override
    public String toString() {
        return "" + id ;
    }

    public Comment() {
    }

    public Comment(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}
