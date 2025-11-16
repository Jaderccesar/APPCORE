package com.example.appcore.model;

import com.example.appcore.enums.AccountType;
import com.example.appcore.enums.FavoriteLanguage;
import com.example.appcore.enums.Gender;
import com.example.appcore.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate; 
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "tb_user")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected  Long id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private String cpf;
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender genero;

    @Enumerated(EnumType.STRING)
    private FavoriteLanguage FavoriteLanguage;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //Reviesar tipo da conta na criação no controller e service, como ele irá escolher que tipo de conta será
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private Status status;

    @Column(updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telephone> phones = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "address_id", nullable = true)
    private Address address;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private ArrayList<Post> posts;

    @OneToOne(cascade = CascadeType.ALL)
    private Timeline timeline;

    @PreUpdate
    public void preUpdate() {
        this.setUpdatedAt(LocalDateTime.now());
    }

    @PrePersist
    public void prePersist() {
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }
}
