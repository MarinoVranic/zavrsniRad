package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Lokacija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LokacijaRepository extends JpaRepository<Lokacija, Long> {

    @Query(value = "SELECT * FROM lokacija l WHERE l.Naziv_lokacije = :nazivLokacije", nativeQuery = true)
    List<Lokacija> findByNazivLokacije(String nazivLokacije);

    @Query(value = "SELECT * FROM lokacija l ORDER BY l.ID_lokacije ASC", nativeQuery = true)
    List<Lokacija> findAll();
}
