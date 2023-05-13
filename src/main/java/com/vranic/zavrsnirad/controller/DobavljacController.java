package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.service.DobavljacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dobavljac")
public class DobavljacController {
    @Autowired
    private DobavljacService dobavljacService;

    @GetMapping("/all")
    public String getAllDobavljaci(Model model){
        model.addAttribute("dobavljaci",dobavljacService.getAllDobavljaci());
        return "dobavljac/dobavljac";
    }

    @GetMapping("/{id}")
    public Dobavljac getDobavljacById(@PathVariable Long id){
        return dobavljacService.getDobavljacById(id);
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable(value = "id") Long id, Model model){
        Dobavljac dobavljac = dobavljacService.getDobavljacById(id);
        model.addAttribute("dobavljac", dobavljac);
        return "dobavljac/updateDobavljac";
    }

    @GetMapping("/addnew")
    public String addNewDobavljac (Model model){
        Dobavljac dobavljac = new Dobavljac();
        model.addAttribute("dobavljac", dobavljac);
        return "dobavljac/newDobavljac";
    }

    @PostMapping("/save")
    public String saveDobavljac(@ModelAttribute("dobavljac") Dobavljac dobavljac){
        dobavljacService.save(dobavljac);
        return "redirect:/dobavljac/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id){
        dobavljacService.deleteById(id);
        return "redirect:/dobavljac/all";
    }
}
