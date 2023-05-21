package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Racun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RacunRepository extends JpaRepository<Racun, String> {
}
