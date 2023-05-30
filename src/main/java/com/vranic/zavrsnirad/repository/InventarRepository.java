package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Inventar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventarRepository extends JpaRepository<Inventar, String> {

    @Query(value = "SELECT COUNT(i.Inventarni_broj) FROM inventar i WHERE i.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    Long checkInvBrojIsFree(String inventarniBroj);

    @Query(value = "SELECT * FROM inventar i WHERE i.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    List<Inventar> findByInventarniBroj(String inventarniBroj);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik")
    List<Inventar> findAll();

//    @Modifying
//    @Query(value = "UPDATE inventar i SET i.Naziv_uredaja = :nazivUredaja, i.Serijski_broj = :serijskiBroj, i.ID_vrste_uredaja = :idVrsteUredaja, i.Hostname = :hostname, " +
//            "i.ID_lokacije = :idLokacije, i.LAN_MAC = :lanMac, i.WiFi_MAC = :wifiMac, i.Warranty_ending = :warrantyEnding, i.ID_racuna = :idRacuna, i.ID_dobavljaca = :idDobavljaca," +
//            " i.Napomena = :napomena WHERE i.inventarniBroj = :inventarniBroj", nativeQuery = true)
//    void editNewWithoutUsername(@Param("nazivUredaja") String nazivUredaja, @Param("serijskiBroj") String serijskiBroj, @Param("idVrsteUredaja") Long idVrsteUredaja,
//                               @Param("hostname") String hostname, @Param("idLokacije") Long idLokacije, @Param("lanMac") String lanMac, @Param("wifiMac") String wifiMac,
//                               @Param("warrantyEnding") LocalDate warrantyEnding, @Param("idRacuna") Long idRacuna, @Param("idDobavljaca") Long idDobavljaca,
//                               @Param("napomena") Long napomena, @Param("napomena") String inventarniBroj);
}
