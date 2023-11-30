package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Inventura;
import com.vranic.zavrsnirad.repository.InventuraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventuraService {

    @Autowired
    private InventuraRepository inventuraRepository;

    public List<Inventura> getAllInventura(){
        return inventuraRepository.findALl();
    }

    public void save(Inventura inventura){
        inventuraRepository.save(inventura);
    }

    public void deleteById(Long idInventure){
        inventuraRepository.deleteById(idInventure);
    }

    public Inventura getInventuraById(Long idInventure){
        return inventuraRepository.findById(idInventure).orElse(null);
    }
}
