package com.example.appcore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "tb_student") 
public class Student extends User {

    private int totalScore;
    private int nivel;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Certificate> certificates;

    @ManyToOne
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
