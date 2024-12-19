package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventura")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventure;
    private String nazivInventure;
}
