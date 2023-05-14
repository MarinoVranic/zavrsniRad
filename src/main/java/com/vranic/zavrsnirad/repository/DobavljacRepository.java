package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Dobavljac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DobavljacRepository extends JpaRepository<Dobavljac, Long> {
    @Query(value = "SELECT * FROM dobavljac d WHERE d.Naziv_dobavljaca = :nazivDobavljaca", nativeQuery = true)
    List<Dobavljac> findByNazivDobavljaca(String nazivDobavljaca);
}
