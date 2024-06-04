package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KorisnikRepository extends JpaRepository<Korisnik, String> {

    @Query(value = "SELECT korisnik FROM Korisnik korisnik WHERE korisnik.lastName LIKE CONCAT('%', :lastName, '%') ORDER BY korisnik.firstName ASC")
    List<Korisnik> findKorisnikByLastName(String lastName);

    @Query(value = "SELECT korisnik FROM Korisnik korisnik WHERE korisnik.firstName = :firstName ORDER BY korisnik.lastName ASC")
    List<Korisnik> findKorisnikByFirstName(String firstName);

    @Query(value = "SELECT korisnik FROM Korisnik korisnik ORDER BY korisnik.userCreated DESC")
    List<Korisnik> findAll();

    @Query(value = "SELECT COUNT(k.Username) FROM korisnik k WHERE k.Username = :username", nativeQuery = true)
    Long checkUsernameIsFree(String username);

    @Query(value = "SELECT * FROM korisnik k WHERE k.Status = 'aktivan' ORDER BY k.User_created DESC", nativeQuery = true)
    List<Korisnik> showAllActive();

    @Query(value = "SELECT * FROM korisnik k WHERE k.Status = 'neaktivan' ORDER BY k.User_disabled DESC", nativeQuery = true)
    List<Korisnik> showAllInactive();

    @Modifying
    @Query(value = "UPDATE korisnik k SET k.Status ='neaktivan', k.User_disabled = :userDisabled, k.Email_disabled = :emailDisabled WHERE k.Username = :username", nativeQuery = true)
    void deactivateKorisnik(@Param("userDisabled") LocalDate userDisabled, @Param("emailDisabled") LocalDate emailDisabled, @Param("username") String username);
}
