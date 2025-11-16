package com.example.appcore.model;

import com.example.appcore.enums.TimelineVisibility;
import com.example.appcore.enums.TypeInteration;
import jakarta.persistence.*;
import lombok.*;

import java.beans.Visibility;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_timeline")
public class Timeline {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String description;
    private LocalDateTime time;
    @Enumerated(EnumType.STRING)
    private TypeInteration TypeInteration;
    @Enumerated(EnumType.STRING)
    private TimelineVisibility visibility;

    @OneToOne
    @JoinColumn(name = "user_id") 
    private Student user;
}
