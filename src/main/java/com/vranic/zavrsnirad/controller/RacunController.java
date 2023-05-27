package com.vranic.zavrsnirad.controller;


import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.model.Racun;
import com.vranic.zavrsnirad.service.RacunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/racun")
public class RacunController {
    @Autowired
    private RacunService racunService;

    @GetMapping("/all")
    public String getAllRacun(Model model) {
        model.addAttribute("sviRacuni", racunService.getAllRacun());
        return "racun/racun";
    }

    @GetMapping("/{brojRacuna}")
    public Racun getRacunById(@PathVariable String brojRacuna) {
        return racunService.getRacunById(brojRacuna);
    }

    @GetMapping("/update/{brojRacuna}")
    public String updateLokacija(@PathVariable(value = "brojRacuna") String brojRacuna, Model model) {
        Racun racun = racunService.getRacunById(brojRacuna);
        model.addAttribute("racun", racun);
        return "lokacija/updateLokacija";
    }

    @GetMapping("/addNew")
    public String addNewLokacija(Model model){
        Racun racun = new Racun();
        model.addAttribute("lokacija", racun);
        return "racun/newRacun";
    }

    @PostMapping("/addNew")
    public String addLokacija(@ModelAttribute("lokacija") Racun racun, Model model) {
        if(racunService.checkIfBrojRacunaIsAvailable(racun.getBrojRacuna())!=0){
            model.addAttribute("error", "Račun pod tim brojem već postoji!");
            return "racun/newRacun";
        }else {
            racunService.save(racun);
        }
        return "redirect:/racun/all";
    }

    @PostMapping("/save")
    public String saveLokacija(@ModelAttribute("racun") Racun racun, Model model) {
        if(racunService.checkIfBrojRacunaIsAvailable(racun.getBrojRacuna())!=0)
        {
            model.addAttribute("error", "Račun pod tim brojem već postoji!");
            return "racun/updateRacun";
        }
        racunService.save(racun);
        return "redirect:/racun/all";
    }

    @GetMapping("delete/{brojRacuna}")
    public String deleteById(@PathVariable(value = "brojRacuna") String brojRacuna) {
        racunService.deleteById(brojRacuna);
        return "redirect:/racun/all";
    }

    @GetMapping("/find")
    public String findDobavljacByName(@RequestParam("brojRacuna") String brojRacuna, Model model) {
        List<Racun> racunList = racunService.findRacunByBrojRacuna(brojRacuna);
        if (racunList.isEmpty()) {
            model.addAttribute("error", "Račun pod tim brojem ne postoji u sustavu!");
            model.addAttribute("sviRacuni", racunService.getAllRacun());
            Racun racun = new Racun();
            model.addAttribute("racun", racun);
        } else {
            model.addAttribute("sviRacuni", racunList);
            Racun racun = new Racun();
            model.addAttribute("racun", racun);
        }
        return "racun/racun";
    }
}
