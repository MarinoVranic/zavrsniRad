package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dobavljac")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dobavljac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDobavljaca;
    private String nazivDobavljaca;
}
