package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Inventura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventuraRepository extends JpaRepository<Inventura, Long> {

    @Query(value = "SELECT * FROM inventura i ORDER BY i.ID_inventure ASC", nativeQuery = true)
    List<Inventura> findALl();

    @Query(value = "SELECT * FROM inventura i WHERE i.ID_inventure = :idInventure", nativeQuery = true)
    List<Inventura> findByIdInventure(Long idInventure);

    @Query(value = "SELECT COUNT(i.ID_inventure) FROM inventura i WHERE i.ID_inventure = :idInventure", nativeQuery = true)
    Long checkIfInventuraExists(Long idInventure);
}
