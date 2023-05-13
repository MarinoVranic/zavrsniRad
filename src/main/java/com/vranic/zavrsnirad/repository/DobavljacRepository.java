package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Dobavljac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DobavljacRepository extends JpaRepository<Dobavljac, Long> {
}
