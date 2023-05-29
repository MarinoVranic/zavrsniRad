package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.model.Lokacija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
