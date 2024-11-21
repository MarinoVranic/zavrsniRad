package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "korisnik")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Korisnik {
    @Id
    private String username;
    @Pattern(regexp = "^[a-zA-ZčćžšđČĆŽŠĐ -]+$", message = "Ime ne smije sadržavati broj niti specijalne znakove!")
    private String firstName;
    @Pattern(regexp = "^[a-zA-ZčćžšđČĆŽŠĐ -]+$", message = "Prezime ne smije sadržavati broj niti specijalne znakove!")
    private String lastName;
    private String status;
    private String accountType;
    @ManyToOne
    @JoinColumn(name = "id_company")
    private Company company;
    @Pattern(regexp = "^[0-9]*$", message = "Godina zaposlenja smije sadržavati samo brojeve!")
    private String godina;
    private String email;
    private String initialPassword;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate userCreated;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate userDisabled;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate emailCreated;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate emailDisabled;
    private String komentar;
}
