package com.vranic.zavrsnirad.repository;


import com.vranic.zavrsnirad.model.MobilnaTarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MobilnaTarifaRepository extends JpaRepository<MobilnaTarifa, Long> {
    @Query(value = "SELECT tarifa FROM MobilnaTarifa tarifa WHERE tarifa.nazivTarife LIKE CONCAT('%', :nazivTarife, '%')")
    List<MobilnaTarifa> findByNazivTarife(String nazivTarife);

    @Query(value = "SELECT tarifa FROM MobilnaTarifa tarifa ORDER BY tarifa.idTarife ASC")
    List<MobilnaTarifa> findAll();

    @Query(value = "SELECT COUNT(tarifa.nazivTarife) FROM MobilnaTarifa tarifa WHERE tarifa.nazivTarife = :nazivTarife")
    Long checkNazivTarifeIsFree(String nazivTarife);
}
