package model;

import enums.AccountType;
import enums.FavoriteLanguage;
import enums.Gender;
import enums.Status;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String cpf;
    private LocalDate birthday;
    @Enumerated(EnumType.STRING)
    private Gender genero;
    @Enumerated(EnumType.STRING)
    private FavoriteLanguage FavoriteLanguage;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Telephone> phones;

    @OneToMany(MappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;
}
