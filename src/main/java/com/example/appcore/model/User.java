package com.example.appcore.model;

import com.example.appcore.enums.AccountType;
import com.example.appcore.enums.FavoriteLanguage;
import com.example.appcore.enums.Gender;
import com.example.appcore.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Student.class, name = "STUDENT"),
        @JsonSubTypes.Type(value = Teacher.class, name = "TEACHER")
})
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
    @Column(name = "account_type")
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
    @JsonManagedReference("user-phones")
    private List<Telephone> phones = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "address_id", nullable = true)
    private Address address;

    @OneToMany(mappedBy = "author")
    @JsonBackReference("post-author") 
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timeline> timeline = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    @JsonBackReference("comment-author")
    private List<Comment> comments = new ArrayList<>();

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
