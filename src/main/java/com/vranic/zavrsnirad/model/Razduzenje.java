package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "razduzenje")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Razduzenje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRazduzenja;

    @ManyToOne
    @JoinColumn(name = "Inventarni_broj")
    private Inventar inventar;

    @ManyToOne
    @JoinColumn(name = "id_zaduzene_lokacija")
    private Lokacija lokacija;

    @ManyToOne
    @JoinColumn(name = "username_zaduzenog")
    private Korisnik korisnik;

    private LocalDate datumZaduzenja;
    private LocalDate datumRazduzenja;

    @ManyToOne
    @JoinColumn(name = "id_usera")
    private User user;
}
