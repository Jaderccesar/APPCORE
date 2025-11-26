package com.example.appcore.model;

import com.example.appcore.enums.PhoneType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tb_telephone") 
public class Telephone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private PhoneType phoneType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-phones")
    private User user;
}
