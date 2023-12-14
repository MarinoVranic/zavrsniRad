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
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAll();

    @Modifying
    @Query(value = "UPDATE inventar i SET i.Hostname = :hostname, i.ID_lokacije = :idLokacije, i.Username = :username, i.Datum_zaduzenja = :datumZaduzenja," +
            "i.Datum_razduzenja = null WHERE i.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    void zaduziInventar(@Param("hostname") String hostname, @Param("idLokacije") Long idLokacije, @Param("username") String username, @Param("datumZaduzenja") LocalDate datumZaduzenja,
                        @Param("inventarniBroj") String inventarniBroj);

    @Modifying
    @Query(value = "UPDATE inventar i SET i.Hostname = null, i.ID_lokacije = 5, i.Username = null, i.Datum_zaduzenja = null," +
            "i.Datum_razduzenja = :datumRazduzenja WHERE i.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    void razduziInventar(@Param("datumRazduzenja") LocalDate datumRazduzenja, @Param("inventarniBroj") String inventarniBroj);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja vu LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE vu.idVrsteUredaja = :idVrstaUredaja ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllByVrstaUredaja(@Param("idVrstaUredaja") Long idVrstaUredaja);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja LEFT JOIN FETCH inventar.lokacija i" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE i.idLokacije = :idLokacije ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllByLokacija(@Param("idLokacije") Long idLokacije);

    @Query(value = "SELECT inventar FROM Inventar inventar LEFT JOIN FETCH inventar.vrstaUredaja vu LEFT JOIN FETCH inventar.lokacija" +
            " LEFT JOIN FETCH inventar.racun LEFT JOIN FETCH inventar.dobavljac LEFT JOIN inventar.korisnik korisnik WHERE vu.nazivVrsteUredaja IN ('laptop', 'pc') ORDER BY inventar.inventarniBroj ASC")
    List<Inventar> findAllIT();
}
