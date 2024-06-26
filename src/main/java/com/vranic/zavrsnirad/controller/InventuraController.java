package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Inventura;
import com.vranic.zavrsnirad.service.InventuraService;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inventura")
public class InventuraController {
    @Autowired
    private InventuraService inventuraService;

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "admin/inventura";
        } else {
            // User has user role
            return "inventura/inventura";
        }
    }

    @GetMapping("/all")
    public String getAllInventura(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("sveInventure", inventuraService.getAllInventura());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/addNew")
    public String addNewInventura(Model model){
        Inventura inventura = new Inventura();
        model.addAttribute("inventura", inventura);
        return "inventura/newInventura";
    }

    @PostMapping("/addNew")
    public String addInventura(@ModelAttribute("inventura") Inventura inventura, Model model){
        if (inventuraService.checkIfInventuraIsExisting(inventura.getIdInventure())!=0) {
            model.addAttribute("error", "Inventura već postoji!");
            return "inventura/newInventura";
        }else{
            inventuraService.save(inventura);
        }
        return "redirect:/inventura/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id){
        inventuraService.deleteById(id);
        return "redirect:/inventura/all";
    }

    @GetMapping("/find")
    public String findInventuraWithId(@RequestParam("idInventure") Long idInventure, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventura> inventuraList = inventuraService.findInventuraById(idInventure);
        if(inventuraList.isEmpty()){
            model.addAttribute("error", "Inventura ne postoji!");
            model.addAttribute("sveInventure", inventuraService.getAllInventura());
            Inventura inventura = new Inventura();
            model.addAttribute("inventura", inventura);
        } else {
            model.addAttribute("sveInventure", inventuraList);
            Inventura inventura = new Inventura();
            model.addAttribute("inventura", inventura);
        }
        return getViewBasedOnRole(auth);
    }
}
