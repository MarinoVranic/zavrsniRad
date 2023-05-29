package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;
@Entity
@Table(name = "racun")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Racun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRacuna;
    private String brojRacuna;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumRacuna;
}
