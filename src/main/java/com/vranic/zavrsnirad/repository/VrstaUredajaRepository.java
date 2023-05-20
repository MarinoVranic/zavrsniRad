package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.VrstaUredaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VrstaUredajaRepository extends JpaRepository<VrstaUredaja, Long> {

    @Query(value = "SELECT * FROM vrsta_uredaja vr WHERE vr.Naziv_vrste_uredaja = :nazivVrsteUredaja", nativeQuery = true)
    List<VrstaUredaja> findByNazivVrsteUredaja(String nazivVrsteUredaja);
}
