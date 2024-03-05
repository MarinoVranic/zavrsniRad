package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Razduzenje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RazduzenjeRepository extends JpaRepository<Razduzenje, Long> {
    @Query(value = "SELECT razduzenje FROM Razduzenje razduzenje LEFT JOIN FETCH razduzenje.inventar LEFT JOIN FETCH razduzenje.lokacija LEFT JOIN FETCH razduzenje.korisnik ORDER BY razduzenje.idRazduzenja ASC")
    List<Razduzenje> findAll();
}
