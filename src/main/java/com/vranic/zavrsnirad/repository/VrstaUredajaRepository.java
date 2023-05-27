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

    @Query(value = "SELECT * FROM vrsta_uredaja vr ORDER BY vr.ID_vrste_uredaja ASC", nativeQuery = true)
    List<VrstaUredaja> findAll();

    @Query(value = "SELECT  COUNT(vr.Naziv_vrste_uredaja) FROM vrsta_uredaja vr WHERE vr.Naziv_vrste_uredaja = :nazivVrsteUredaja", nativeQuery = true)
    Long checkNazivVrsteUredajaIsFree(String nazivVrsteUredaja);
}
