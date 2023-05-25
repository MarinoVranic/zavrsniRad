package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.repository.LokacijaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LokacijaService {

    @Autowired
    private LokacijaRepository lokacijaRepository;

    public List<Lokacija> getAllLokacija(){
        return lokacijaRepository.findAll();
    }

    public Lokacija getLokacijaById(Long idLokacije){
        return lokacijaRepository.findById(idLokacije).orElse(null);
    }

    public void save(Lokacija lokacija){
        lokacijaRepository.save(lokacija);
    }

    public void deleteById(Long idLokacije){
        lokacijaRepository.deleteById(idLokacije);
    }

    public List<Lokacija> findLokacijaByName(String nazivLokacije){
        return lokacijaRepository.findByNazivLokacije(nazivLokacije);
    }

    public Long checkIfNazivLokacijeIsAvailable(String nazivLokacije){
        return lokacijaRepository.checkNazivLokacijeIsFree(nazivLokacije);
    }
}
