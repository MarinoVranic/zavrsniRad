package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.service.InventarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inventar")
public class InventarController {
    @Autowired
    private InventarService inventarService;

    @GetMapping("/all")
    public String getAllInventar(Model model) {
        model.addAttribute("savInventar", inventarService.getAllInventar());
        System.out.println(inventarService.getAllInventar());
        return "inventar/inventar";
    }

    @GetMapping("/{inventarniBroj}")
    public Inventar getInventarById(@PathVariable String inventarniBroj) {
        return inventarService.getInventarById(inventarniBroj);
    }

    @GetMapping("/update/{inventarniBroj}")
    public String updateInventar(@PathVariable(value = "inventarniBroj") String inventarniBroj, Model model) {
        Inventar inventar = inventarService.getInventarById(inventarniBroj);
        model.addAttribute("inventar", inventar);
        return "inventar/updateInventar";
    }

    @GetMapping("/addNew")
    public String addNewInventar(Model model){
        Inventar inventar = new Inventar();
        model.addAttribute("inventar", inventar);
        return "inventar/newInventar";
    }

    @PostMapping("/addNew")
    public String addInventar(@ModelAttribute("inventar") Inventar inventar, Model model) {
        if(inventarService.checkIfInvBrojIsAvailable(inventar.getInventarniBroj())!=0){
            model.addAttribute("error", "Inventarni broj već postoji!");
            return "inventar/newInventar";
        }else {
            inventarService.save(inventar);
        }
        return "redirect:/inventar/all";
    }

    @PostMapping("/save")
    public String saveInventar(@ModelAttribute("inventar") Inventar inventar, Model model) {
        if(inventarService.checkIfInvBrojIsAvailable(inventar.getInventarniBroj())!=0)
        {
            model.addAttribute("error", "Inventarni broj već postoji!");
            return "inventar/updateInventar";
        }
        inventarService.save(inventar);
        return "redirect:/inventar/all";
    }

    @GetMapping("delete/{inventarniBroj}")
    public String deleteById(@PathVariable(value = "inventarniBroj") String inventarniBroj) {
        inventarService.deleteById(inventarniBroj);
        return "redirect:/inventar/all";
    }

    @GetMapping("/find")
    public String findInventarByName(@RequestParam("inventarniBroj") String inventarniBroj, Model model) {
        List<Inventar> inventarList = inventarService.findInventarByInvBroj(inventarniBroj);
        if (inventarList.isEmpty()) {
            model.addAttribute("error", "Inventar pod tim brojem ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getAllInventar());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        }
        return "inventar/inventar";
    }
}
