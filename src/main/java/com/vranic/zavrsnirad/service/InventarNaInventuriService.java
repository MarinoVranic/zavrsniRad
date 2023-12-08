package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.InventarNaInventuri;
import com.vranic.zavrsnirad.repository.InventarNaInventuriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarNaInventuriService {

    @Autowired
    private InventarNaInventuriRepository inventarNaInventuriRepository;

    public List<InventarNaInventuri> getAllInventarNaInventuri(){
        return inventarNaInventuriRepository.findAll();
    }

    public void save(InventarNaInventuri inventarNaInventuri){
        inventarNaInventuriRepository.save(inventarNaInventuri);
    }
    public void deleteById(Long skeniraniId){
        inventarNaInventuriRepository.deleteById(skeniraniId);
    }

    public List<InventarNaInventuri> findByInventarniBroj(String inventarniBroj){
        return inventarNaInventuriRepository.findByInvBroj(inventarniBroj);
    }

    public List<InventarNaInventuri> finbByGodinaInventure(Long idInventure){
        return inventarNaInventuriRepository.findByGodInventure(idInventure);
    }

    public Long checkIfInventarAlreadyScanned(String inventarniBroj, Long idInventure){
        return inventarNaInventuriRepository.checkIfInvBrojIsScanned(inventarniBroj, idInventure);
    }
}
