package com.example.appcore.model;

import com.example.appcore.enums.PostStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter 
@ToString
@EqualsAndHashCode
@AllArgsConstructor 
@NoArgsConstructor
@Table(name = "tb_post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createDate = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference("post-author")
    private User author; 

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference("post-comments")
    private List<Comment> comments;

}
