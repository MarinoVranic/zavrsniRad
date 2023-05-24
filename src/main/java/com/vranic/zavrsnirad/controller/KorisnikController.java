package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.service.KorisnikService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/korisnik")
public class KorisnikController {
    @Autowired
    private KorisnikService korisnikService;

    @GetMapping("/all")
    public String getAllKorisnik(Model model){
        model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
//        Korisnik korisnik = new Korisnik();
//        model.addAttribute("korisnik", korisnik);
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


    @PostMapping("/save")
    public String saveKorisnik(@ModelAttribute("korisnik") @Valid Korisnik korisnik, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            System.out.println(bindingResult);
            return "korisnik/newKorisnik";
        } else if (korisnikService.checkIfUsernameIsFree(korisnik.getUsername())!=0) {
            model.addAttribute("error", "Korisničko ime već postoji!");
            return "korisnik/newKorisnik";
        }else
            korisnikService.save(korisnik);
            return "redirect:/korisnik/all";
    }

//    @PostMapping("/addNew")
//    public String saveKorisnik(@ModelAttribute("korisnik") @Valid Korisnik korisnik, BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            System.out.println(bindingResult);
//            return "korisnik/newKorisnik";
//        }
//        korisnikService.save(korisnik);
//        return "redirect:/korisnik/all";
//    }

//    @PostMapping("/save")
//    public String saveKorisnik(@Valid Korisnik korisnik, BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            System.out.println(bindingResult);
//            return "korisnik/newKorisnik";
//        }
//        korisnikService.save(korisnik);
//        return "redirect:/korisnik/all";
//    }


//    @GetMapping("/find")
//    public String findKorisnikByLastName(@PathVariable(value = "lastName") String lastName, Model model) {
//        System.out.println(lastName);
//        List<Korisnik> korisnikList = korisnikService.findKorisnikByLastName(lastName);
//        if (korisnikList.isEmpty()) {
//            model.addAttribute("error", "Lokacija tog naziva ne postoji u sustavu!");
//            model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
////            Korisnik korisnik = new Korisnik();
////            model.addAttribute("korisnik", korisnik);
//        } else {
//            model.addAttribute("sveLokacije", korisnikList);
////            Korisnik korisnik = new Korisnik();
////            model.addAttribute("korisnik", korisnik);
////            System.out.println(lokacijaList.get(0));
//        }
//        return "korisnik/korisnik";
//    }

    @GetMapping("/find")
    public String findKorisnikByLastName(@RequestParam("lastName") String lastName, Model model) {
//        System.out.println(lastName);
        List<Korisnik> korisnikList = korisnikService.findKorisnikByLastName(lastName);
        if (korisnikList.isEmpty()) {
            model.addAttribute("error", "Korisnik/ici tog prezimena nisu pronađeni!");
            model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
//            Korisnik korisnik = new Korisnik();
//            model.addAttribute("korisnik", korisnik);
        } else {
            model.addAttribute("sviKorisnici", korisnikList);
//            Korisnik korisnik = new Korisnik();
//            model.addAttribute("korisnik", korisnik);
//            System.out.println(lokacijaList.get(0));
        }
        return "korisnik/korisnik";
    }
}
