package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "inventar")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventar {
    @Id
    private String inventarniBroj;
    private String nazivUredaja;
    private String serijskiBroj;

    @ManyToOne
    @JoinColumn(name = "id_vrste_uredaja")
    private VrstaUredaja vrstaUredaja;

    private String hostname;

    @ManyToOne
    @JoinColumn(name = "id_lokacije")
    private Lokacija lokacija;

    @ManyToOne(optional = true)
    @JoinColumn(name = "username")
    private Korisnik korisnik;

    private String lanMac;
    private String wifiMac;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumZaduzenja;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumRazduzenja;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyEnding;

    @ManyToOne
    @JoinColumn(name = "id_racuna")
    private Racun racun;

    @ManyToOne
    @JoinColumn(name = "id_dobavljaca")
    private Dobavljac dobavljac;

    private String napomena;
}
