package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Racun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RacunRepository extends JpaRepository<Racun, String> {

    @Query(value = "SELECT * FROM racun r WHERE r.Broj_racuna = :brojRacuna", nativeQuery = true)
    List<Racun> findByBrojRacuna(String brojRacuna);
}
