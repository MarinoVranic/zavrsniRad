package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.service.KorisnikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/korisnik")
public class KorisnikController {
    @Autowired
    private KorisnikService korisnikService;

    @GetMapping("/all")
    public String getAllKorisnik(Model model){
        model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
        Korisnik korisnik = new Korisnik();
        model.addAttribute("korisnik", korisnik);
        return "korisnik/korisnik";
    }

    @GetMapping("/{username}")
    public Korisnik getKorisnikById(@PathVariable String username){
        return korisnikService.getKorisnikById(username);
    }

    @GetMapping("/update/{username}")
    public String updateKorisnik(@PathVariable(value = "username") String username, Model model){
        Korisnik korisnik = korisnikService.getKorisnikById(username);
        model.addAttribute("korisnik", korisnik);
        return "korisnik/updateKorisnik";
    }

    @GetMapping("delete/{username}")
    public String deleteById(@PathVariable(value = "username")String username){
        korisnikService.deleteById(username);
        return "redirect:/korisnik/all";
    }
}
