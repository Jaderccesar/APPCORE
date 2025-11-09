package com.example.appcore.appcore.model;

import com.example.appcore.appcore.enums.CreateStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    private CreateStatus status;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private Enterprise responsible;

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        createDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }
}
