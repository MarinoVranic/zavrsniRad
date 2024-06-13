package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.repository.KorisnikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KorisnikService {
    @Autowired
    private KorisnikRepository korisnikRepository;

    public List<Korisnik> getAllKorisnik(){
        return korisnikRepository.findAll();
    }

    public Korisnik getKorisnikById(String username){
        return korisnikRepository.findById(username).orElse(null);
    }

    public void save(Korisnik korisnik){
        korisnikRepository.save(korisnik);
    }

    public void deleteById(String username){
        korisnikRepository.deleteById(username);
    }

    public List<Korisnik> findKorisnikByLastName(String lastName){
        return korisnikRepository.findKorisnikByLastName(lastName);
    }

    public List<Korisnik> findKorisnikByFirstName(String firstName){
        return korisnikRepository.findKorisnikByFirstName(firstName);
    }

    public List<Korisnik> findKorisnikByPoslodavac(String subcontractor){
        return korisnikRepository.findKorisnikByPoslodavac(subcontractor);
    }

    public Long checkIfUsernameIsFree(String username){
        return korisnikRepository.checkUsernameIsFree(username);
    }

    public List<Korisnik> getAllActiveKorisnik(){
        return korisnikRepository.showAllActive();
    }

    public List<Korisnik> getAllInactiveKorisnik(){
        return korisnikRepository.showAllInactive();
    }

    public void deactivateKorisnik(LocalDate userDisabled, LocalDate emailDisabled, String username){
        korisnikRepository.deactivateKorisnik(userDisabled, emailDisabled, username);
    }

    public String createPasswordStudent(String usernameFirstChar, String usernameSecondChar, LocalDate today){
        String year = Integer.toString(today.getYear());
        return usernameFirstChar+year.substring(0,2)+"AitStd"+year.substring(2,4)+usernameSecondChar;
    }

    public String createPasswordZaposlenik(String usernameFirstChar, String usernameSecondChar, LocalDate today){
        String year = Integer.toString(today.getYear());
        return usernameFirstChar+year.substring(0,2)+"Cat1@"+year.substring(2,4)+usernameSecondChar;
    }

    public String createPasswordKooperant(String usernameFirstChar, String usernameSecondChar, LocalDate today){
        String year = Integer.toString(today.getYear());
        return usernameFirstChar+year.substring(0,2)+"Koop@1t"+year.substring(2,4)+usernameSecondChar;
    }
}
