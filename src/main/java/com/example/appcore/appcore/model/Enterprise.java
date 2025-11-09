package com.example.appcore.appcore.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tb_enterprise")
public class Enterprise extends User{

    private String fantasyName;
    private String cnpj;

    @OneToMany(mappedBy = "responsible", cascade = CascadeType.ALL, orphanRemoval = true)
    private ArrayList<Event> promotedEvents;
}
