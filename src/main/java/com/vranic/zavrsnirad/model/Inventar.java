package com.vranic.zavrsnirad.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long idVrsteUredaja;
    private String hostname;
    private Long idLokacije;
    private String username;
    private String lanMac;
    private String wifiMac;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumZaduzenja;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumRazduzenja;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyEnding;
    private String brojRacuna;
    private Long idDobavljaca;
    private String napomena;
}
