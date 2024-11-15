package com.vranic.zavrsnirad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "inventar")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SqlResultSetMapping(
        name = "InventarMapping",
        entities = @EntityResult(
                entityClass = Inventar.class,
                fields = {
                        @FieldResult(name = "inventarniBroj", column = "inventarni_broj"),
                        @FieldResult(name = "nazivUredaja", column = "naziv_uredaja"),
                        @FieldResult(name = "serijskiBroj", column = "serijski_broj"),
                        @FieldResult(name = "vrstaUredaja", column = "id_vrste_uredaja"),
                        @FieldResult(name = "hostname", column = "hostname"),
                        @FieldResult(name = "lokacija", column = "id_lokacije"),
                        @FieldResult(name = "korisnik", column = "username"),
                        @FieldResult(name = "lanMac", column = "lan_mac"),
                        @FieldResult(name = "wifiMac", column = "wifi_mac"),
                        @FieldResult(name = "datumZaduzenja", column = "datum_zaduzenja"),
                        @FieldResult(name = "datumRazduzenja", column = "datum_razduzenja"),
                        @FieldResult(name = "warrantyEnding", column = "warranty_ending"),
                        @FieldResult(name = "racun", column = "id_racuna"),
                        @FieldResult(name = "dobavljac", column = "id_dobavljaca"),
                        @FieldResult(name = "napomena", column = "napomena"),
                        @FieldResult(name = "nabavnaVrijednost", column = "nabavna_vrijednost")
                }
        )
)
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Inventar.reportOSByInventuraAndNotFound",
                query = "SELECT inventar.* FROM inventar inventar " +
                        "LEFT JOIN inventar_na_inventuri ini ON inventar.inventarni_broj = ini.inventarni_broj AND ini.id_inventure = :idInventure " +
                        "WHERE ini.inventarni_broj IS NULL AND inventar.inventarni_broj NOT LIKE 'SI%' AND inventar.inventarni_broj NOT LIKE 'IT%'" +
                        "ORDER BY CASE " +
                        "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
                        "ELSE '' " +
                        "END, " +
                        "CAST(CASE " +
                        "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
                        "ELSE inventar.inventarni_broj " +
                        "END AS UNSIGNED) DESC, " +
                        "inventar.inventarni_broj DESC",
                resultSetMapping = "InventarMapping"
        ),
        @NamedNativeQuery(
                name = "Inventar.reportSIByInventuraAndNotFound",
                query = "SELECT inventar.* FROM inventar inventar " +
                        "LEFT JOIN inventar_na_inventuri ini ON inventar.inventarni_broj = ini.inventarni_broj AND ini.id_inventure = :idInventure " +
                        "WHERE ini.inventarni_broj IS NULL AND inventar.inventarni_broj LIKE 'SI%'" +
                        "ORDER BY CASE " +
                        "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
                        "ELSE '' " +
                        "END, " +
                        "CAST(CASE " +
                        "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
                        "ELSE inventar.inventarni_broj " +
                        "END AS UNSIGNED) ASC, " +
                        "inventar.inventarni_broj ASC",
                resultSetMapping = "InventarMapping"
        )
})

public class Inventar {
    @Id
    private String inventarniBroj;
    private String nazivUredaja;
    private String serijskiBroj;

    @ManyToOne
    @JoinColumn(name = "id_vrste_uredaja")
    private VrstaUredaja vrstaUredaja;

    private String hostname;

    @ManyToOne
    @JoinColumn(name = "id_lokacije")
    private Lokacija lokacija;

    @ManyToOne(optional = true)
    @JoinColumn(name = "username")
    private Korisnik korisnik;

    private String lanMac;
    private String wifiMac;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumZaduzenja;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datumRazduzenja;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyEnding;

    @ManyToOne
    @JoinColumn(name = "id_racuna")
    private Racun racun;

    @ManyToOne
    @JoinColumn(name = "id_dobavljaca")
    private Dobavljac dobavljac;

    private String napomena;
    private BigDecimal nabavnaVrijednost;
    private String brojMobitela;

    @ManyToOne
    @JoinColumn(name = "id_tarife")
    private MobilnaTarifa mobilnaTarifa;

    private boolean razlikaCijenePlacena;
}
