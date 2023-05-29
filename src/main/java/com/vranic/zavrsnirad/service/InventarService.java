package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.repository.InventarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarService {
    @Autowired
    private InventarRepository inventarRepository;

    public List<Inventar> getAllInventar(){
        return inventarRepository.findAll();
    }

    public Inventar getInventarById(String inventarniBroj){
        return inventarRepository.findById(inventarniBroj).orElse(null);
    }

    public void save(Inventar inventar){
        inventarRepository.save(inventar);
    }

    public void deleteById(String inventarniBroj){
        inventarRepository.deleteById(inventarniBroj);
    }

    public Long checkIfInvBrojIsAvailable(String inventarniBroj){
        return inventarRepository.checkInvBrojIsFree(inventarniBroj);
    }

    public List<Inventar> findInventarByInvBroj(String inventarniBroj){
        return inventarRepository.findByInventarniBroj(inventarniBroj);
    }
}
