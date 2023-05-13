package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Lokacija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LokacijaRepository extends JpaRepository<Lokacija, Long> {
}
