package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.model.VrstaUredaja;
import com.vranic.zavrsnirad.service.VrstaUredajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/vrstaUredaja")
public class VrstaUredajaController {
    @Autowired
    private VrstaUredajaService vrstaUredajaService;

    @GetMapping("/all")
    public String getAllVrstaUredaja(Model model) {
        model.addAttribute("sveVrsteUredaja", vrstaUredajaService.getAllVrstaUredaja());
        return "vrstaUredaja/vrstaUredaja";
    }

    @GetMapping("/{id}")
    public VrstaUredaja getVrstaUredajaById(@PathVariable Long id) {
        return vrstaUredajaService.getVrstaUredajaById(id);
    }

    @GetMapping("/update/{id}")
    public String updateVrstaUredaja(@PathVariable(value = "id") Long id, Model model) {
        VrstaUredaja vrstaUredaja = vrstaUredajaService.getVrstaUredajaById(id);
        model.addAttribute("vrstaUredaja", vrstaUredaja);
        return "vrstaUredaja/updateVrstaUredaja";
    }

    @GetMapping("/addNew")
    public String addNewVrstaUredaja(Model model){
        VrstaUredaja vrstaUredaja = new VrstaUredaja();
        model.addAttribute("vrstaUredaja", vrstaUredaja);
        return "vrstaUredaja/newVrstaUredaja";
    }

    @PostMapping("/addNew")
    public String addVrstaUredaja(@ModelAttribute("vrstaUredaja") VrstaUredaja vrstaUredaja, Model model) {
        if(vrstaUredajaService.checkIfNazivVrsteUredajaIsAvailable(vrstaUredaja.getNazivVrsteUredaja())!=0){
            model.addAttribute("error", "Vrsta uređaja tog naziva već postoji!");
            return "vrstaUredaja/newVrstaUredaja";
        }else {
            vrstaUredajaService.save(vrstaUredaja);
        }
        return "redirect:/vrstaUredaja/all";
    }

    @PostMapping("/save")
    public String saveVrstaUredaja(@ModelAttribute("vrstaUredaja") VrstaUredaja vrstaUredaja, Model model) {
        if(vrstaUredajaService.checkIfNazivVrsteUredajaIsAvailable(vrstaUredaja.getNazivVrsteUredaja())!=0)
        {
            model.addAttribute("error", "Vrsta uređaja tog naziva već postoji!");
            return "vrstaUredaja/updateVrstaUredaja";
        }
        vrstaUredajaService.save(vrstaUredaja);
        return "redirect:/vrstaUredaja/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id) {
        vrstaUredajaService.deleteById(id);
        return "redirect:/vrstaUredaja/all";
    }

    @GetMapping("/find")
    public String findVrstaUredajaByName(@RequestParam("nazivVrsteUredaja") String nazivVrsteUredaja, Model model) {
        List<VrstaUredaja> vrstaUredajaList = vrstaUredajaService.findVrstaUredajaByName(nazivVrsteUredaja);
        if (vrstaUredajaList.isEmpty()) {
            model.addAttribute("error", "Vrsta uređaja tog naziva ne postoji u sustavu!");
            model.addAttribute("sveVrsteUredaja", vrstaUredajaService.getAllVrstaUredaja());
            VrstaUredaja vrstaUredaja = new VrstaUredaja();
            model.addAttribute("vrstaUredaja", vrstaUredaja);
        } else {
            model.addAttribute("sveVrsteUredaja", vrstaUredajaList);
            VrstaUredaja vrstaUredaja = new VrstaUredaja();
            model.addAttribute("vrstaUredaja", vrstaUredaja);
        }
        return "vrstaUredaja/vrstaUredaja";
    }
}
