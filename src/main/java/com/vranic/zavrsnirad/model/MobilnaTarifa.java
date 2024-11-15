package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mobilna_tarifa")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobilnaTarifa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarife;
    private String nazivTarife;
}
