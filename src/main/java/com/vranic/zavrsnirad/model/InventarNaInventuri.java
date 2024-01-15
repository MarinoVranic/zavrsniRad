package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.*;

@Entity
@Table(name = "inventar_na_inventuri")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarNaInventuri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSkeniranja;

    @ManyToOne
    @JoinColumn(name = "Inventarni_broj")
    private Inventar inventar;

    @ManyToOne
    @JoinColumn(name = "id_inventure")
    private Inventura inventura;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime datumSkeniranja;

    @ManyToOne
    @JoinColumn(name = "id_skenirane_lokacije")
    private Lokacija lokacija;
}
