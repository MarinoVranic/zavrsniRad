package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Inventar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarRepository extends JpaRepository<Inventar, String> {
}
