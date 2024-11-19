package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.repository.InventarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public void zaduziInventar(String hostname, Long idLokacije, String username, LocalDate datumZaduzenja, String inventarniBroj){
        inventarRepository.zaduziInventar(hostname, idLokacije, username, datumZaduzenja, inventarniBroj);
    }

    public void razduziInventar(LocalDate datumRazduzenja, String inventarniBroj){
        inventarRepository.razduziInventar(datumRazduzenja, inventarniBroj);
    }

    public List<Inventar> getInventarByVrstaUredaja(Long idVrsteUredaja){
        return inventarRepository.findAllByVrstaUredaja(idVrsteUredaja);
    }

    public List<Inventar> getInventarByLokacija(Long idLokacije){
        return inventarRepository.findAllByLokacija(idLokacije);
    }

    public List<Inventar> getInventarBySI(){
        return inventarRepository.findAllBySI();
    }

    public List<Inventar> getInventarByLS(){
        return inventarRepository.findAllByLS();
    }

    public List<Inventar> getInventarByOS(){
        return inventarRepository.findAllByOS();
    }

    public List<Inventar> getInventarForIT(){
        return inventarRepository.findAllIT();
    }

    public List<Inventar> getInventarByIT(){
        return inventarRepository.findAllByIT();
    }

    public List<Inventar> getInventarBySerialNumber(String serijskiBroj){
        return inventarRepository.findAllBySerijskiBroj(serijskiBroj);
    }

    public List<Inventar> getInventarByMacAddress(String addressMac){
        return inventarRepository.findAllByMAC(addressMac);
    }

    public List<Inventar> getInventarByUser(String lastName){
        return inventarRepository.findAllByUser(lastName);
    }

    public List<Inventar> getInventarForAdministration(){
        return inventarRepository.findAllForAdministration();
    }

    //all for mobiles
    public List<Inventar> getAllMobitel(){
        return inventarRepository.findAllMobitel();
    }

    public void razlikaPlacena(String inventarniBroj){
        inventarRepository.razlikaPlacena(inventarniBroj);
    }

    public List<Inventar> getMobitelByLokacija(Long idLokacije){
        return inventarRepository.findMobitelByLokacija(idLokacije);
    }

    public List<Inventar> getMobitelByUser(String lastname){
        return inventarRepository.findMobitelByUser(lastname);
    }

    public List<Inventar> getMobitelBySerijskiBroj(String serijskiBroj){
        return inventarRepository.findMobileBySerijskiBroj(serijskiBroj);
    }

    public List<Inventar> getMobitelByTarifa(Long idTarife){
        return inventarRepository.findMobileByTarifa(idTarife);
    }

    public String getLastInventarniBrojIT(){
        return inventarRepository.lastInventarniBrojIT();
    }

    public String getLastInventarniBrojLS(){
        return inventarRepository.lastInventarniBrojLS();
    }

    public String getLastInventarniBrojSI(){
        return inventarRepository.lastInventarniBrojSI();
    }
}
