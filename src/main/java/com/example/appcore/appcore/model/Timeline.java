package com.example.appcore.appcore.model;

import com.example.appcore.appcore.enums.TimelineVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.beans.Visibility;
import java.time.LocalDateTime;

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
    private TimelineVisibility visibility;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Student user;
}
