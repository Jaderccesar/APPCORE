package com.example.appcore.appcore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PreUpdate;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Student extends User {

    private int totalScore;
    private int nivel;
    //private ArrayList<achievement> achievements;
}
