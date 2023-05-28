package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.service.KorisnikService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/korisnik")
public class KorisnikController {
    @Autowired
    private KorisnikService korisnikService;

    @GetMapping("/all")
    public String getAllKorisnik(Model model){
        model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
        return "korisnik/korisnik";
    }

    @GetMapping("/active")
    public String getAllActiveKorisnik(Model model){
        model.addAttribute("sviKorisnici", korisnikService.getAllActiveKorisnik());
        return "korisnik/korisnik";
    }

    @GetMapping("/inactive")
    public String getAllInactiveKorisnik(Model model){
        model.addAttribute("sviKorisnici", korisnikService.getAllInactiveKorisnik());
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

    @GetMapping("/addNew")
    public String addNewKorisnik(Model model){
        Korisnik korisnik = new Korisnik();
        model.addAttribute("korisnik", korisnik);
        return "korisnik/newKorisnik";
    }


    @PostMapping("/addNew")
    public String addKorisnik(@ModelAttribute("korisnik") @Valid Korisnik korisnik, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "korisnik/newKorisnik";
        } else if (korisnikService.checkIfUsernameIsFree(korisnik.getUsername())!=0) {
            model.addAttribute("error", "Korisničko ime već postoji!");
            return "korisnik/newKorisnik";
        }else
            korisnikService.save(korisnik);
            return "redirect:/korisnik/all";
    }

    @PostMapping("/save")
    public String saveKorisnik(@ModelAttribute("korisnik") @Valid Korisnik korisnik, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            System.out.println(bindingResult);
            return "korisnik/updateKorisnik";
        }
            korisnikService.save(korisnik);
        return "redirect:/korisnik/all";
    }

    @GetMapping("deactivate/{username}")
    @Transactional
    public String deactivateKorisnik(@PathVariable(value = "username")String username){
        Korisnik korisnik = korisnikService.getKorisnikById(username);
        LocalDate today = LocalDate.now();
        korisnikService.deactivateKorisnik(today, today, username);
        System.out.println(korisnik.getUserDisabled());
        System.out.println(korisnik.getEmailDisabled());
        System.out.println(username);
        return "redirect:/korisnik/all";
    }

    @GetMapping("/find")
    public String findKorisnikByLastName(@RequestParam("lastName") String lastName, Model model) {
        List<Korisnik> korisnikList = korisnikService.findKorisnikByLastName(lastName);
        if (korisnikList.isEmpty()) {
            model.addAttribute("error", "Korisnik/ici tog prezimena nisu pronađeni!");
            model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
        } else {
            model.addAttribute("sviKorisnici", korisnikList);
        }
        return "korisnik/korisnik";
    }
}
