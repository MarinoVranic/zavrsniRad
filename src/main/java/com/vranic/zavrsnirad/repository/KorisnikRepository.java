package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, String> {
}
