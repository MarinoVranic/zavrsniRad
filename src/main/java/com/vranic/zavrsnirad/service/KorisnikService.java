package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.repository.KorisnikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}