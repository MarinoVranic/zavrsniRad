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
@Table(name = "racun")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Racun {
    @Id
    private Long idRacuna;
    private String brojRacuna;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumRacuna;
}
