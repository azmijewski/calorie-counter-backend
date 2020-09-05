package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@NamedQueries({
        @NamedQuery(name = "user.FindByUserName", query = "SELECT u from User u where u.username = :username"),
        @NamedQuery(name = "user.FindByToken", query = "SELECT u from User u where u.token = :token"),
        @NamedQuery(name = "user.FindByLoginOrEmail", query = "SELECT u from User u where u.username = :username or u.email = :email"),
        @NamedQuery(name = "user.FindAll", query = "SELECT u from User u")
})

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(name = "userId")
    private Long id;
    private String username;
    @Email
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Double weight;
    private Boolean isConfirmed;
    private String token;
    private Double height;
    private Double calorie;
    private LocalDate birthDate;
    @CreationTimestamp
    @Column(updatable = false)
    private Date timestamp;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserProduct> userProducts = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Meal> meals = new ArrayList<>();
}
