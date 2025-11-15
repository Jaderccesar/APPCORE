package com.example.appcore.model;

import com.example.appcore.enums.RankType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_ranking")
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RankType type;
    private LocalDate updateDate;

    @OneToMany(mappedBy = "ranking", cascade = CascadeType.ALL)
    private ArrayList<Student> participants;
}
