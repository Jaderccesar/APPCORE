package com.example.appcore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode 
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_teacher") 
public class Teacher extends User{

    private String specializedArea;

    @OneToMany
    @JoinColumn(name = "course_id")
    private List<Course> ministredCourses;

    @OneToMany(mappedBy = "responsible", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Challenge> challenges;
}
