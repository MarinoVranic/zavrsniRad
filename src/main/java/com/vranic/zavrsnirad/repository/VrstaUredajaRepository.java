package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.VrstaUredaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VrstaUredajaRepository extends JpaRepository<VrstaUredaja, Long> {
}
