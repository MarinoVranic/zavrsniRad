package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Racun;
import com.vranic.zavrsnirad.repository.RacunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class RacunService {
    @Autowired
    private RacunRepository racunRepository;

    public List<Racun> getAllRacun(){
        return racunRepository.findAll();
    }

    public Racun getRacunById (Long idRacuna){
        return racunRepository.findById(idRacuna).orElse(null);
    }

    public void save(Racun racun)  {
        racunRepository.save(racun);
    }

    public void deleteById(Long idRacuna){
        racunRepository.deleteById(idRacuna);
    }

    public List<Racun> findRacunByBrojRacuna(String brojRacuna){
        return racunRepository.findByBrojRacuna(brojRacuna);
    }

    public List<Racun> findRacunByBrojUre(String brojUre){
        return racunRepository.findByBrojUre(brojUre);
    }

    public Long checkIfBrojRacunaIsAvailable(String brojRacuna){
        return racunRepository.checkBrojRacunaIsFree(brojRacuna);
    }
}
