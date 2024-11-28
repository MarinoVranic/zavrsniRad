package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Razduzenje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RazduzenjeRepository extends JpaRepository<Razduzenje, Long> {
    @Query(value = "SELECT razduzenje FROM Razduzenje razduzenje LEFT JOIN FETCH razduzenje.inventar LEFT JOIN FETCH razduzenje.lokacija LEFT JOIN FETCH razduzenje.korisnik ORDER BY razduzenje.idRazduzenja DESC")
    List<Razduzenje> findAll();

    @Query(value = "SELECT razduzenje FROM Razduzenje razduzenje LEFT JOIN FETCH razduzenje.inventar LEFT JOIN FETCH razduzenje.lokacija LEFT JOIN FETCH razduzenje.korisnik WHERE razduzenje.inventar.inventarniBroj = :inventarniBroj ORDER BY razduzenje.idRazduzenja ASC")
    List<Razduzenje> findAllByInventarniBroj(@Param("inventarniBroj") String inventarniBroj);

    @Query(value = "SELECT razduzenje FROM Razduzenje razduzenje LEFT JOIN FETCH razduzenje.inventar LEFT JOIN FETCH razduzenje.lokacija LEFT JOIN FETCH razduzenje.korisnik WHERE razduzenje.korisnik.username = :username ORDER BY razduzenje.idRazduzenja DESC")
    List<Razduzenje> findAllByUser(@Param("username") String username);
}
