package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.*;
import com.vranic.zavrsnirad.service.KorisnikService;
import com.vranic.zavrsnirad.service.RazduzenjeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/razduzenje")
public class RazduzenjeController {

    @Autowired
    private RazduzenjeService razduzenjeService;

    @Autowired
    private KorisnikService korisnikService;

    @GetMapping("/all")
    public String getAllRazduzenje(Model model){
        model.addAttribute("svaRazduzenja", razduzenjeService.getAllRazduzenje());
        model.addAttribute("allKorisnik", korisnikService.getAllKorisnik());
        return "razduzenje/razduzenje";
    }

    @GetMapping("/delete/{idRazduzenja}")
    public String deleteById(@PathVariable(value = "idRazduzenja") Long idRazduzenja){
        razduzenjeService.deleteById(idRazduzenja);
        return "redirect:/razduzenje/all";
    }

    @GetMapping("/find")
    public String findRazduzenjeByInventarniBroj(@RequestParam("inventarniBroj") String inventarniBroj, Model model) {
        List<Razduzenje> razduzenjeList = razduzenjeService.getAllByInventarniBroj(inventarniBroj);
        if (razduzenjeList.isEmpty()) {
            model.addAttribute("error", "Inventar pod tim brojem ne postoji u sustavu!");
            model.addAttribute("svaRazduzenja", razduzenjeService.getAllRazduzenje());
            Razduzenje razduzenje = new Razduzenje();
            model.addAttribute("razduzenje", razduzenje);
        } else {
            model.addAttribute("svaRazduzenja", razduzenjeList);
            Razduzenje razduzenje = new Razduzenje();
            model.addAttribute("razduzenje", razduzenje);
        }
        return "razduzenje/razduzenje";
    }

    @GetMapping("/findByUser")
    public String showRazduzenjaByUser(@RequestParam("username") String username, Model model) {
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        List<Razduzenje> razduzenjeList = razduzenjeService.getAllByUser(username);
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("svaRazduzenja", razduzenjeList);
        Razduzenje razduzenje = new Razduzenje();
        model.addAttribute("razduzenje", razduzenje);
        return "razduzenje/razduzenje";
    }
}
