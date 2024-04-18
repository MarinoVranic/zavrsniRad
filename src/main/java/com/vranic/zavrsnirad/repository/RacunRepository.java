package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Racun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RacunRepository extends JpaRepository<Racun, Long> {

    @Query(value = "SELECT * FROM racun r WHERE r.Broj_racuna = :brojRacuna", nativeQuery = true)
    List<Racun> findByBrojRacuna(String brojRacuna);

    @Query(value = "SELECT racun FROM Racun racun WHERE racun.brojUre LIKE CONCAT('%', :brojUre, '%')")
    List<Racun> findByBrojUre(String brojUre);

    @Query(value = "SELECT * FROM racun r ORDER BY r.Datum_racuna DESC", nativeQuery = true)
    List<Racun> findAll();

    @Query(value = "SELECT COUNT(r.Broj_racuna) FROM racun r WHERE r.Broj_racuna = :brojRacuna", nativeQuery = true)
    Long checkBrojRacunaIsFree(String brojRacuna);
}
