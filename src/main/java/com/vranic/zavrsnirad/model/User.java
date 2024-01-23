package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;
    private boolean active;
    private String roles;
    @Pattern(regexp = "^[a-zA-ZčćžšđČĆŽŠĐ -]+$", message = "Ime ne smije sadržavati broj niti specijalne znakove!")
    private String firstName;
    @Pattern(regexp = "^[a-zA-ZčćžšđČĆŽŠĐ -]+$", message = "Prezime ne smije sadržavati broj niti specijalne znakove!")
    private String lastName;
}
