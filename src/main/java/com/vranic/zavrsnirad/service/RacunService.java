package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Racun;
import com.vranic.zavrsnirad.repository.RacunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.DateFormatter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Service
public class RacunService {
    @Autowired
    private RacunRepository racunRepository;

    public List<Racun> getAllRacun(){
        return racunRepository.findAll();
    }

    public Racun getRacunById(String brojRacuna){
        return racunRepository.findById(brojRacuna).orElse(null);
    }

    public void save(Racun racun)  {
        racunRepository.save(racun);
    }

    public void deleteById(String brojRacuna){
        racunRepository.deleteById(brojRacuna);
    }

    public List<Racun> findRacunByBrojRacuna(String brojRacuna){
        return racunRepository.findByBrojRacuna(brojRacuna);
    }
}
