package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.service.InventarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventar")
public class InventarController {
    @Autowired
    private InventarService inventarService;

    @GetMapping("/all")
    public String getAllInventar(Model model){
        model.addAttribute("savInventar", inventarService.getAllInventar());
        Inventar inventar = new Inventar();
        model.addAttribute("inventar", inventar);
        return "inventar/inventar";
    }

    @GetMapping("/{inventarniBroj}")
    public Inventar getInventarById(@PathVariable String inventarniBroj){
        return inventarService.getInventarById(inventarniBroj);
    }

    @PostMapping("/update")
    public String updateInventar(@ModelAttribute("inventar") Inventar inventar){
        inventarService.save(inventar);
        return "redirect:/inventar/all";
    }

    @GetMapping("delete/{inventarniBroj}")
    public String deleteById(@PathVariable(value = "inventarniBroj")String inventarniBroj){
        inventarService.deleteById(inventarniBroj);
        return "redirect:/inventar/all";
    }
}
