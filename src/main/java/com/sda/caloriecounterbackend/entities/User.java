package com.sda.caloriecounterbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@NamedQueries({
        @NamedQuery(name = "user.FindByUserName", query = "SELECT u from User u where u.username = :username")
})

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Integer age;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<UserProduct> userProducts = new ArrayList<>();
}
