package com.vranic.zavrsnirad.controller;


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

    @GetMapping("/{id}")
    public Racun getRacunById(@PathVariable Long id) {
        return racunService.getRacunById(id);
    }

    @GetMapping("/update/{id}")
    public String updateRacun(@PathVariable(value = "id") Long id, Model model) {
        Racun racun = racunService.getRacunById(id);
        model.addAttribute("racun", racun);
        return "racun/updateRacun";
    }

    @GetMapping("/addNew")
    public String addNewRacun(Model model){
        Racun racun = new Racun();
        model.addAttribute("racun", racun);
        return "racun/newRacun";
    }

    @PostMapping("/addNew")
    public String addRacun(@ModelAttribute("racun") Racun racun, Model model) {
        if(racunService.checkIfBrojRacunaIsAvailable(racun.getBrojRacuna())!=0){
            model.addAttribute("error", "Broj računa već postoji!");
            return "racun/newRacun";
        }else {
            racunService.save(racun);
        }
        return "redirect:/racun/all";
    }

    @PostMapping("/save")
    public String saveRacun(@ModelAttribute("racun") Racun racun, Model model) {
        if(racunService.checkIfBrojRacunaIsAvailable(racun.getBrojRacuna())!=0)
        {
            model.addAttribute("error", "Broj računa već postoji!");
            return "racun/updateRacun";
        }
        racunService.save(racun);
        return "redirect:/racun/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id) {
        racunService.deleteById(id);
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
