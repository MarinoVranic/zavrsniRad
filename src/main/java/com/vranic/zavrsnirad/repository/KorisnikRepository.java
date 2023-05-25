package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, String> {

    @Query(value="SELECT * FROM korisnik k WHERE k.Last_name = :lastName ORDER BY k.First_name", nativeQuery = true)
    List<Korisnik> findKorisnikByLastName(String lastName);

    @Query(value = "SELECT * FROM korisnik k ORDER BY k.Last_name ASC", nativeQuery = true)
    List<Korisnik> findAll();

    @Query(value = "SELECT COUNT(k.Username) FROM korisnik k WHERE k.Username = :username", nativeQuery = true)
    Long checkUsernameIsFree(String username);
}
