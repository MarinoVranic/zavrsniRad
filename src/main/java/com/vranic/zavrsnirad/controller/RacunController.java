package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.model.Racun;
import com.vranic.zavrsnirad.service.RacunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/racun")
public class RacunController {
    @Autowired
    private RacunService racunService;

    @GetMapping("/all")
    public String getAllRacun(Model model){
        model.addAttribute("sviRacuni", racunService.getAllRacun());
        Racun racun = new Racun();
        model.addAttribute("racun", racun);
        return "racun/racun";
    }


    @GetMapping("/{id}")
    public Racun getRacunById(@PathVariable String brojRacuna) {
        return racunService.getRacunById(brojRacuna);
    }

    @PostMapping("/update")
    public String updateRacun(Racun racun, Model model) {
        racunService.save(racun);
        model.addAttribute("racun", racun);
        return "redirect:/racun/all";
    }

    @PostMapping("/save")
    public String saveRacun(@ModelAttribute("racun") Racun racun) {
        racunService.save(racun);
        return "redirect:/racun/all";
    }

    @GetMapping("/delete")
    public String deleteById(String brojRacuna) {
        racunService.deleteById(brojRacuna);
        return "redirect:/racun/all";
    }

    @GetMapping("/find")
    public String findRacunByName(@RequestParam("brojRacuna") String brojRacuna, Model model) {
//        System.out.println(brojRacuna);
        List<Racun> racunList = racunService.findRacunByBrojRacuna(brojRacuna);
//        System.out.println(racunList);
        if (racunList.isEmpty()) {
            model.addAttribute("error", "Račun pod tim brojem ne postoji u sustavu!");
            model.addAttribute("sviRacuni", racunService.getAllRacun());
            Racun racun = new Racun();
            model.addAttribute("racun", racun);
        } else {
            model.addAttribute("sviRacuni", racunList);
            Racun racun = new Racun();
            model.addAttribute("racun", racun);
//            System.out.println(lokacijaList.get(0));
        }
        return "racun/racun";
    }
}
