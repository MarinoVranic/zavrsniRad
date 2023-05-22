package com.vranic.zavrsnirad.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "korisnik")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Korisnik {
    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private String accountType;
    private String subcontractor;
    private Integer godina;
    private String email;
    private String initialPassword;
    private LocalDate userCreated;
    private LocalDate userDisabled;
    private LocalDate emailCreated;
    private LocalDate emailDisabled;
    private String komentar;
}
