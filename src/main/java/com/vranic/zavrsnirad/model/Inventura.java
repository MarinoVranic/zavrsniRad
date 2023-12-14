package com.vranic.zavrsnirad.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long idInventure;
}
