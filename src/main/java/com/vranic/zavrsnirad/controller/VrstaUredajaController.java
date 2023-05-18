package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.VrstaUredaja;
import com.vranic.zavrsnirad.service.VrstaUredajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vrstaUredaja")
public class VrstaUredajaController {
    @Autowired
    private VrstaUredajaService vrstaUredajaService;

    @GetMapping("/all")
    public String getAllVrstaUredaja(Model model){
        model.addAttribute("vrstauredaja", vrstaUredajaService.getAllVrstaUredaja());
        VrstaUredaja vrstaUredaja = new VrstaUredaja();
        model.addAttribute("novaVrsta", vrstaUredaja);
        return "vrstaUredaja/vrstaUredaja";
    }

    @GetMapping("/{id}")
    public VrstaUredaja getVrstaUredajaById(@PathVariable Long id){
        return vrstaUredajaService.getVrstaUredajaById(id);
    }

    @GetMapping("/update/{id}")
    public String updateVrstaUredaja(@PathVariable(value = "id") Long id, Model model){
        VrstaUredaja vrstaUredaja = vrstaUredajaService.getVrstaUredajaById(id);
        model.addAttribute("vrstauredaja",vrstaUredaja);
        return "vrstaUredaja/updateVrstaUredaja";
    }

//    @GetMapping("/addNew")
//    public String addNewVrstaUredaja(Model model){
//        VrstaUredaja vrstaUredaja = new VrstaUredaja();
//        model.addAttribute("vrstauredaja", vrstaUredaja);
//        return "vrstaUredaja/newVrstaUredaja";
//    }

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
}
