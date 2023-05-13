package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.service.LokacijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lokacija")
public class LokacijaController {
    @Autowired
    private LokacijaService lokacijaService;

    @GetMapping("/all")
    public String getAllLokacija(Model model){
        model.addAttribute("lokacija",lokacijaService.getAllLokacija());
        return "lokacija/lokacija";
    }

    @GetMapping("/{id}")
    public Lokacija getLokacijaById(@PathVariable Long id){
        return lokacijaService.getLokacijaById(id);
    }

    @GetMapping("/update/{id}")
    public String updateLokacija(@PathVariable(value = "id") Long id, Model model){
        Lokacija lokacija = lokacijaService.getLokacijaById(id);
        model.addAttribute("lokacija", lokacija);
        return "lokacija/updateLokacija";
    }

    @GetMapping("/addNew")
    public String addNewLokacija(Model model){
        Lokacija lokacija = new Lokacija();
        model.addAttribute("lokacija", lokacija);
        return "lokacija/newLokacija";
    }

    @PostMapping("/save")
    public String saveLokacija(@ModelAttribute("lokacija") Lokacija lokacija){
        lokacijaService.save(lokacija);
        return "redirect:/lokacija/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id){
        lokacijaService.deleteById(id);
        return "redirect:/lokacija/all";
    }
}
