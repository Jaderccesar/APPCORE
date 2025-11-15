package com.example.appcore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "tb_student") 
public class Student extends User {

    private int totalScore;
    private int nivel;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private ArrayList<Certificate> certificates;

    @ManyToOne
    @JoinColumn(name = "ranking_id")
    private Ranking ranking;
    //private ArrayList<achievement> achievements;
}
