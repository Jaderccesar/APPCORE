package com.example.appcore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "tb_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content;
    private String authorName;
    private Double rating;
    private String response;
    private Long respondedBy;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "course_id") 
    private Course course;
 
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference("post-comments")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference("comment-author")
    private User author;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
