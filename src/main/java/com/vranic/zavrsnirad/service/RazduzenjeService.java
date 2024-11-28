package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Razduzenje;
import com.vranic.zavrsnirad.repository.RazduzenjeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RazduzenjeService {

    @Autowired
    private RazduzenjeRepository razduzenjeRepository;

    public List<Razduzenje> getAllRazduzenje(){
        return razduzenjeRepository.findAll();
    }

    public void save(Razduzenje razduzenje){
        razduzenjeRepository.save(razduzenje);
    }

    public void deleteById(Long idRazduzenja){
        razduzenjeRepository.deleteById(idRazduzenja);
    }

    public Razduzenje getById(Long idRazduzenja) {
        return razduzenjeRepository.findById(idRazduzenja).orElse(null);
    }

    public List<Razduzenje> getAllByInventarniBroj(String inventarniBroj){
        return razduzenjeRepository.findAllByInventarniBroj(inventarniBroj);
    }

    public List<Razduzenje> getAllByUser(String username){
        return razduzenjeRepository.findAllByUser(username);
    }
}
