package com.vranic.zavrsnirad.controller;

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
    public String getAllVrstaUredaja(Model model){
        model.addAttribute("sveVrsteUredaja", vrstaUredajaService.getAllVrstaUredaja());
        VrstaUredaja vrstaUredaja = new VrstaUredaja();
        model.addAttribute("vrstauredaja", vrstaUredaja);
        return "vrstaUredaja/vrstaUredaja";
    }
    @GetMapping("/{id}")
    public VrstaUredaja getVrstaUredajaById(@PathVariable Long id){
        return vrstaUredajaService.getVrstaUredajaById(id);
    }
    @PostMapping("/update")
    public String updateVrstaUredaja(VrstaUredaja vrstaUredaja, Model model){
        vrstaUredajaService.save(vrstaUredaja);
        model.addAttribute("vrstauredaja",vrstaUredaja);
        return "redirect:/vrstaUredaja/all";
    }

    @PostMapping("/save")
    public String saveVrstaUredaja(@ModelAttribute("vrstauredaja") VrstaUredaja vrstaUredaja){
        vrstaUredajaService.save(vrstaUredaja);
        return "redirect:/vrstaUredaja/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteVrstaUredajaById(@PathVariable(value = "id") Long id){
        vrstaUredajaService.deleteById(id);
        return "redirect:/vrstaUredaja/all";
    }

    @GetMapping("/find")
    public String findDobavljacByName(@RequestParam("nazivVrsteUredaja") String nazivVrsteUredaja, Model model) {
        List<VrstaUredaja> vrstaUredajaList = vrstaUredajaService.findVrstaUredajaByName(nazivVrsteUredaja);
        if (vrstaUredajaList.isEmpty()) {
            model.addAttribute("error", "Vrsta uređaja tog naziva ne postoji u sustavu!");
            model.addAttribute("sveVrsteUredaja", vrstaUredajaService.getAllVrstaUredaja());
            VrstaUredaja vrstaUredaja = new VrstaUredaja();
            model.addAttribute("vrstauredaja", vrstaUredaja);
        } else {
            model.addAttribute("sveVrsteUredaja", vrstaUredajaList);
            VrstaUredaja vrstaUredaja = new VrstaUredaja();
            model.addAttribute("vrstauredaja", vrstaUredaja);
//            System.out.println(vrstaUredajaList.get(0));
        }
        return "vrstaUredaja/vrstaUredaja";
    }
}
