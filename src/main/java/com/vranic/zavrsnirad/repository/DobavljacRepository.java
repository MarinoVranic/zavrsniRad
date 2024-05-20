package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Dobavljac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DobavljacRepository extends JpaRepository<Dobavljac, Long> {

    @Query(value = "SELECT dobavljac FROM Dobavljac dobavljac WHERE dobavljac.nazivDobavljaca LIKE CONCAT('%', :nazivDobavljaca, '%')")
    List<Dobavljac> findByNazivDobavljaca(String nazivDobavljaca);

    @Query(value = "SELECT * FROM dobavljac d ORDER BY d.ID_dobavljaca ASC", nativeQuery = true)
    List<Dobavljac> findAll();

    @Query(value = "SELECT COUNT(d.Naziv_dobavljaca) FROM dobavljac d WHERE d.Naziv_dobavljaca = :nazivDobavljaca", nativeQuery = true)
    Long checkNazivDobavljacaIsFree(String nazivDobavljaca);
}
