package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.repository.DobavljacRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DobavljacService {
    @Autowired
    private DobavljacRepository dobavljacRepository;
    public List<Dobavljac> getAllDobavljaci(){
        return dobavljacRepository.findAll();
    }

    public Dobavljac getDobavljacById(Long idDobavljaca){
        return dobavljacRepository.findById(idDobavljaca).orElse(null);
    }

    public void save(Dobavljac dobavljac){
        dobavljacRepository.save(dobavljac);
    }

    public void deleteById(Long idDobavljaca){
        dobavljacRepository.deleteById(idDobavljaca);
    }

    public List<Dobavljac> findDobavljacByName(String nazivDobavljaca){
        return dobavljacRepository.findByNazivDobavljaca(nazivDobavljaca);
    }

    public Long checkIfNazivDobavljacaIsAvailable(String nazivDobavljaca){
        return dobavljacRepository.checkNazivDobavljacaIsFree(nazivDobavljaca);
    }
}
