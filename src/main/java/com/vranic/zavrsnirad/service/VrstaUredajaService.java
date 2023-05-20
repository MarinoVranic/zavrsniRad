package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.VrstaUredaja;
import com.vranic.zavrsnirad.repository.VrstaUredajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class VrstaUredajaService {
    @Autowired
    private VrstaUredajaRepository vrstaUredajaRepository;

    public List<VrstaUredaja> getAllVrstaUredaja(){
        return vrstaUredajaRepository.findAll();
    }

    public VrstaUredaja getVrstaUredajaById(Long idVrsteUredaja){
        return vrstaUredajaRepository.findById(idVrsteUredaja).orElse(null);
    }

    public void save(VrstaUredaja vrstaUredaja){
        vrstaUredajaRepository.save(vrstaUredaja);
    }

    public void deleteById(Long idVrsteUredaja){
        vrstaUredajaRepository.deleteById(idVrsteUredaja);
    }

    public List<VrstaUredaja> findVrstaUredajaByName(String nazivVrsteUredaja){
        return vrstaUredajaRepository.findByNazivVrsteUredaja(nazivVrsteUredaja);
    }
}
