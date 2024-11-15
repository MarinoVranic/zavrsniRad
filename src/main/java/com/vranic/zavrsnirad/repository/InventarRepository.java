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

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vrstaUredaja ON inventar.id_vrste_uredaja = vrstaUredaja.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
    List<Inventar> findAll();

    @Modifying
    @Query(value = "UPDATE inventar i SET i.Hostname = :hostname, i.ID_lokacije = :idLokacije, i.Username = :username, i.Datum_zaduzenja = :datumZaduzenja," +
            "i.Datum_razduzenja = null WHERE i.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    void zaduziInventar(@Param("hostname") String hostname, @Param("idLokacije") Long idLokacije, @Param("username") String username, @Param("datumZaduzenja") LocalDate datumZaduzenja,
                        @Param("inventarniBroj") String inventarniBroj);

    @Modifying
    @Query(value = "UPDATE inventar i SET i.Hostname = null, i.ID_lokacije = 23, i.Username = null, i.Datum_zaduzenja = null," +
            "i.Datum_razduzenja = :datumRazduzenja WHERE i.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    void razduziInventar(@Param("datumRazduzenja") LocalDate datumRazduzenja, @Param("inventarniBroj") String inventarniBroj);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja vu LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE vu.idVrsteUredaja = :idVrstaUredaja ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllByVrstaUredaja(@Param("idVrstaUredaja") Long idVrstaUredaja);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja LEFT JOIN FETCH inventar.lokacija i" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE i.idLokacije = :idLokacije ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllByLokacija(@Param("idLokacije") Long idLokacije);

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vu ON inventar.id_vrste_uredaja = vu.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
            "WHERE vu.naziv_vrste_uredaja IN ('laptop', 'pc', 'monitor', 'miš', 'slušalice', 'mobitel', 'speakerphone', 'switch') " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
    List<Inventar> findAllIT();

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja vu LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE inventar.inventarniBroj LIKE 'SI%' ORDER BY inventar.inventarniBroj DESC")
    List<Inventar> findAllBySI();

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja vu LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE inventar.inventarniBroj LIKE 'IT%' ORDER BY inventar.inventarniBroj DESC")
    List<Inventar> findAllByIT();

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja vu LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE inventar.inventarniBroj LIKE 'LS%' ORDER BY inventar.inventarniBroj DESC")
    List<Inventar> findAllByLS();

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja vu LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE inventar.inventarniBroj NOT LIKE 'SI%' AND inventar.inventarniBroj NOT LIKE 'IT%' AND inventar.inventarniBroj NOT LIKE 'LS%' ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllByOS();

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vu ON inventar.id_vrste_uredaja = vu.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
            "WHERE inventar.inventarni_broj NOT LIKE 'IT%' " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
        List<Inventar> findAllForAdministration();

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja LEFT JOIN FETCH inventar.lokacija i" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE korisnik.lastName LIKE CONCAT('%', :lastName, '%') ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllByUser(@Param("lastName") String lastName);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja LEFT JOIN FETCH inventar.lokacija i" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE inventar.lanMac LIKE CONCAT('%', :addressMac, '%') OR inventar.wifiMac LIKE CONCAT('%', :addressMac, '%') ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllByMAC(@Param("addressMac") String addressMac);

    List<Inventar> findAllBySerijskiBroj(@Param("serijskiBroj") String serijskiBroj);

    //All for mobiles

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vu ON inventar.id_vrste_uredaja = vu.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
            "WHERE vu.naziv_vrste_uredaja IN ('mobitel') " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
    List<Inventar> findAllMobitel();

    @Modifying
    @Query(value = "UPDATE Inventar inventar SET inventar.razlikaCijenePlacena=true WHERE inventar.inventarniBroj = :inventarniBroj")
    void razlikaPlacena(@Param("inventarniBroj") String inventarniBroj);

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vu ON inventar.id_vrste_uredaja = vu.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
//            "LEFT JOIN mobilna_tarifa tarifa ON inventar.id_tarife = tarifa.id_tarife" +
            "WHERE vu.naziv_vrste_uredaja IN ('mobitel') " +
            "AND lokacija.id_lokacije = :idLokacije " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
    List<Inventar> findMobitelByLokacija(@Param("idLokacije") Long idLokacije);

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vu ON inventar.id_vrste_uredaja = vu.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
//            "LEFT JOIN mobilna_tarifa tarifa ON inventar.id_tarife = tarifa.id_tarife" +
            "WHERE vu.naziv_vrste_uredaja IN ('mobitel') " +
            "AND korisnik.last_name LIKE CONCAT('%', :lastName, '%') " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
    List<Inventar> findMobitelByUser(@Param("lastName") String lastName);

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vu ON inventar.id_vrste_uredaja = vu.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
//            "LEFT JOIN mobilna_tarifa tarifa ON inventar.id_tarife = tarifa.id_tarife" +
            "WHERE vu.naziv_vrste_uredaja IN ('mobitel') " +
            "AND inventar.serijski_broj = :serijskiBroj " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
    List<Inventar> findMobileBySerijskiBroj(@Param("serijskiBroj") String serijskiBroj);

    @Query(value = "SELECT inventar.* FROM inventar inventar " +
            "LEFT JOIN vrsta_uredaja vu ON inventar.id_vrste_uredaja = vu.id_vrste_uredaja " +
            "LEFT JOIN lokacija lokacija ON inventar.id_lokacije = lokacija.id_lokacije " +
            "LEFT JOIN racun racun ON inventar.id_racuna = racun.id_racuna " +
            "LEFT JOIN dobavljac dobavljac ON inventar.id_dobavljaca = dobavljac.id_dobavljaca " +
            "LEFT JOIN korisnik korisnik ON inventar.username = korisnik.username " +
//            "LEFT JOIN mobilna_tarifa tarifa ON inventar.id_tarife = tarifa.id_tarife" +
            "WHERE vu.naziv_vrste_uredaja IN ('mobitel') " +
            "AND inventar.id_tarife = :idTarife " +
            "ORDER BY CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN inventar.inventarni_broj " +
            "ELSE '' " +
            "END, " +
            "CAST(CASE " +
            "WHEN inventar.inventarni_broj REGEXP '^[A-Za-z]+.*' THEN '0' " +
            "ELSE inventar.inventarni_broj " +
            "END AS UNSIGNED) DESC, " +
            "inventar.inventarni_broj DESC",
            nativeQuery = true)
    List<Inventar> findMobileByTarifa(@Param("idTarife") Long idTarife);
}
