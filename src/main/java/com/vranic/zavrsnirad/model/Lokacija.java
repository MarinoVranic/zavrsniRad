package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lokacija")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lokacija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLokacije;
    private String nazivLokacije;
}
