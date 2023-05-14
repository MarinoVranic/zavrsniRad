package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vrsta_uredaja")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VrstaUredaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVrsteUredaja;
    private String nazivVrsteUredaja;
}
