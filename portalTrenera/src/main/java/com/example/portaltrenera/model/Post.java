package com.example.portaltrenera.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
public class Post {

    @SequenceGenerator(
            name = "post_sequence",
            sequenceName = "post_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "post_sequence"
    )
    private Long id;
    private Long userId;
    private String title;
    private boolean trainingDone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private LocalDate trainingDay;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "postId", updatable = false, insertable = false)
    private List<Comment> commentList;

    public Post() {}

    public Post(Long userId, String title, boolean trainingDone, LocalDate trainingDay) {
        this.userId = userId;
        this.title = title;
        this.trainingDone = trainingDone;
        this.trainingDay = trainingDay;
        this.commentList = Collections.<Comment>emptyList();
    }
}
