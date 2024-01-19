package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.model.InventarNaInventuri;
import com.vranic.zavrsnirad.repository.InventarNaInventuriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public InventarNaInventuri findById(Long idSkeniranja){
        return inventarNaInventuriRepository.findByIdSkeniranja(idSkeniranja);
    }

    public List<InventarNaInventuri> findByInventarniBroj(String inventarniBroj){
        return inventarNaInventuriRepository.findByInvBroj(inventarniBroj);
    }

    public List<InventarNaInventuri> finbByGodinaInventure(Long idInventure){
        return inventarNaInventuriRepository.findByGodInventure(idInventure);
    }

    public List<InventarNaInventuri> SIByGodinaInventure(Long idInventure){
        return inventarNaInventuriRepository.SIByGodInventure(idInventure);
    }

    public List<InventarNaInventuri> OSByGodinaInventure(Long idInventure){
        return inventarNaInventuriRepository.OSByGodInventure(idInventure);
    }

    public Long checkIfInventarAlreadyScanned(String inventarniBroj, Long idInventure){
        return inventarNaInventuriRepository.checkIfInvBrojIsScanned(inventarniBroj, idInventure);
    }

    public InventarNaInventuri selectInvNaInvByBrojAndInventura(String inventarniBroj, Long idInventure){
        return inventarNaInventuriRepository.selectInvNaInvByBrojAndInventura(inventarniBroj, idInventure);
    }

    public void changeLokacija(Long idTrenutneLokacije, String inventarniBroj){
        inventarNaInventuriRepository.changeLokacija(idTrenutneLokacije, inventarniBroj);
    }

    public void changeStanje(String stanje, Long idSkeniranja){
        inventarNaInventuriRepository.changeStanje(stanje, idSkeniranja);
    }

    public void changeOtpis(String otpis, Long idSkeniranja){
        inventarNaInventuriRepository.changeOtpis(otpis, idSkeniranja);
    }

    public List<Inventar> reportOSByInventuraAndNotFound(Long idInventure) {
        return inventarNaInventuriRepository.reportOSByInventuraAndNotFound(idInventure);
    }

    public List<Inventar> reportSIByInventuraAndNotFound(Long idInventure){
        return inventarNaInventuriRepository.reportSIByInventuraAndNotFound(idInventure);
    }
}
