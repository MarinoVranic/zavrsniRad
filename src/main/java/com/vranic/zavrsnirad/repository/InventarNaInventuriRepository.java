package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.model.InventarNaInventuri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventarNaInventuriRepository extends JpaRepository<InventarNaInventuri, Long> {

    @Query(value = "SELECT inventarNaInventuri FROM InventarNaInventuri inventarNaInventuri LEFT JOIN FETCH inventarNaInventuri.inventar LEFT JOIN FETCH inventarNaInventuri.inventura LEFT JOIN FETCH inventarNaInventuri.lokacija ORDER BY inventarNaInventuri.inventar.inventarniBroj DESC")
    List<InventarNaInventuri> findAll();

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    List<InventarNaInventuri> findByInvBroj(String inventarniBroj);

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.id_skeniranja = :idSkeniranja", nativeQuery = true)
    InventarNaInventuri findByIdSkeniranja(Long idSkeniranja);

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.ID_Inventure = :idInventure", nativeQuery = true)
    List<InventarNaInventuri> findByGodInventure(Long idInventure);

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.ID_Inventure = :idInventure AND ini.Inventarni_broj LIKE 'SI%'", nativeQuery = true)
    List<InventarNaInventuri> SIByGodInventure(Long idInventure);

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.ID_Inventure = :idInventure AND ini.Inventarni_broj NOT LIKE 'SI%'", nativeQuery = true)
    List<InventarNaInventuri> OSByGodInventure(Long idInventure);

    @Query(value = "SELECT COUNT(ini.Inventarni_broj AND ini.ID_Inventure) FROM inventar_na_inventuri ini WHERE ini.Inventarni_broj = :inventarniBroj AND ini.ID_Inventure = :idInventure", nativeQuery = true)
    Long checkIfInvBrojIsScanned(String inventarniBroj, Long idInventure);

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.Inventarni_broj = :inventarniBroj AND ini.ID_Inventure = :idInventure", nativeQuery = true)
    InventarNaInventuri selectInvNaInvByBrojAndInventura(String inventarniBroj, Long idInventure);

    @Modifying
    @Query(value = "UPDATE inventar i SET i.ID_lokacije = :idTrenutneLokacije WHERE i.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    void changeLokacija(@Param("idTrenutneLokacije") Long idTrenutneLokacije, @Param("inventarniBroj") String inventarniBroj);

    @Modifying
    @Query(value = "UPDATE inventar_na_inventuri ini SET ini.stanje = :stanje WHERE ini.id_skeniranja = :idSkeniranja", nativeQuery = true)
    void changeStanje(@Param("stanje") String stanje, @Param("idSkeniranja") Long idSkeniranja);

    @Modifying
    @Query(value = "UPDATE inventar_na_inventuri ini SET ini.otpis = :otpis WHERE ini.id_skeniranja = :idSkeniranja", nativeQuery = true)
    void changeOtpis(@Param("otpis") String otpis, @Param("idSkeniranja") Long idSkeniranja);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN InventarNaInventuri ini ON inventar.inventarniBroj = ini.inventar.inventarniBroj AND ini.inventura.idInventure = :idInventure WHERE ini.inventar.inventarniBroj IS NULL AND inventar.inventarniBroj NOT LIKE 'SI%'")
    List<Inventar> reportOSByInventuraAndNotFound(@Param("idInventure") Long idInventure);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN InventarNaInventuri ini ON inventar.inventarniBroj = ini.inventar.inventarniBroj AND ini.inventura.idInventure = :idInventure WHERE ini.inventar.inventarniBroj IS NULL AND inventar.inventarniBroj LIKE 'SI%'")
    List<Inventar> reportSIByInventuraAndNotFound(@Param("idInventure") Long idInventure);
}
