package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.model.MobilnaTarifa;
import com.vranic.zavrsnirad.repository.MobilnaTarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobilnaTarifaService {
    @Autowired
    private MobilnaTarifaRepository mobilnaTarifaRepository;

    public List<MobilnaTarifa> getAllMobilnaTarifa() {
        return mobilnaTarifaRepository.findAll();
    };

    public MobilnaTarifa getMobilnaTarifaById(Long idDobavljaca){
        return mobilnaTarifaRepository.findById(idDobavljaca).orElse(null);
    }

    public void save(MobilnaTarifa mobilnaTarifa) {
        mobilnaTarifaRepository.save(mobilnaTarifa);
    }

    public void deleteById(Long idMobilneTarife) {
        mobilnaTarifaRepository.deleteById(idMobilneTarife);
    }

    public Long checkIfNazivTarifeIsAvailable(String nazivTarife){
        return mobilnaTarifaRepository.checkNazivTarifeIsFree(nazivTarife);
    }

    public List<MobilnaTarifa> findMobilnaTarifaByName(String nazivTarife){
        return mobilnaTarifaRepository.findByNazivTarife(nazivTarife);
    }
}
