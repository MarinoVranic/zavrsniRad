package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.service.DobavljacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/dobavljac")
public class DobavljacController {
    @Autowired
    private DobavljacService dobavljacService;

    @GetMapping("/all")
    public String getAllDobavljac(Model model) {
        model.addAttribute("sviDobavljaci", dobavljacService.getAllDobavljaci());
        return "dobavljac/dobavljac";
    }

    @GetMapping("/{id}")
    public Dobavljac getDobavljacById(@PathVariable Long id) {
        return dobavljacService.getDobavljacById(id);
    }

    @GetMapping("/update/{id}")
    public String updateDobavljac(@PathVariable(value = "id") Long id, Model model) {
        Dobavljac dobavljac = dobavljacService.getDobavljacById(id);
        model.addAttribute("dobavljac", dobavljac);
        return "dobavljac/updateDobavljac";
    }

    @GetMapping("/addNew")
    public String addNewDobavljac(Model model){
        Dobavljac dobavljac = new Dobavljac();
        model.addAttribute("dobavljac", dobavljac);
        return "dobavljac/newDobavljac";
    }

    @PostMapping("/addNew")
    public String addDobavljac(@ModelAttribute("dobavljac") Dobavljac dobavljac, Model model) {
        if(dobavljacService.checkIfNazivDobavljacaIsAvailable(dobavljac.getNazivDobavljaca())!=0){
            model.addAttribute("error", "Dobavljač tog naziva već postoji!");
            return "dobavljac/newDobavljac";
        }else {
            dobavljacService.save(dobavljac);
        }
        return "redirect:/dobavljac/all";
    }

    @PostMapping("/save")
    public String saveDobavljac(@ModelAttribute("dobavljac") Dobavljac dobavljac, Model model) {
        if(dobavljacService.checkIfNazivDobavljacaIsAvailable(dobavljac.getNazivDobavljaca())!=0)
        {
            model.addAttribute("error", "Dobavljač tog naziva već postoji!");
            return "/dobavljac/updateDobavljac";
        }
        dobavljacService.save(dobavljac);
        return "redirect:/dobavljac/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id) {
        dobavljacService.deleteById(id);
        return "redirect:/dobavljac/all";
    }

    @GetMapping("/find")
    public String findDobavljacByName(@RequestParam("nazivDobavljaca") String nazivDobavljaca, Model model) {
        List<Dobavljac> dobavljacList = dobavljacService.findDobavljacByName(nazivDobavljaca);
        if (dobavljacList.isEmpty()) {
            model.addAttribute("error", "Dobavljač tog naziva ne postoji u sustavu!");
            model.addAttribute("sviDobavljaci", dobavljacService.getAllDobavljaci());
            Dobavljac dobavljac = new Dobavljac();
            model.addAttribute("dobavljac", dobavljac);
        } else {
            model.addAttribute("sviDobavljaci", dobavljacList);
            Dobavljac dobavljac = new Dobavljac();
            model.addAttribute("dobavljac", dobavljac);
        }
        return "dobavljac/dobavljac";
    }
}
