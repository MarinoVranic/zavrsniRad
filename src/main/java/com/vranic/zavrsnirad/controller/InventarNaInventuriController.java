package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.model.InventarNaInventuri;
import com.vranic.zavrsnirad.model.Inventura;
import com.vranic.zavrsnirad.model.VrstaUredaja;
import com.vranic.zavrsnirad.service.InventarNaInventuriService;
import com.vranic.zavrsnirad.service.InventarService;
import com.vranic.zavrsnirad.service.InventuraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/provodenjeInventure")
public class InventarNaInventuriController {

    @Autowired
    private InventarNaInventuriService inventarNaInventuriService;

    @Autowired
    private InventuraService inventuraService;

    @Autowired
    private InventarService inventarService;

    @GetMapping("/all")
    public String getAllInventarNaInventuri(Model model){
        model.addAttribute("savInvNaInventuri", inventarNaInventuriService.getAllInventarNaInventuri());
        List<Inventura> allInventura = inventuraService.getAllInventura();
        model.addAttribute("allInventura", allInventura);
        return "inventura/provodenjeInventure";
    }

    @PostMapping("/addNew")
    public String addNewScan(@RequestParam("invBroj") String inventarniBroj, Model model){
        Integer currentYear = LocalDate.now().getYear();
        Inventar inventar = inventarService.getInventarById(inventarniBroj);
        Inventura inventura = inventuraService.getInventuraById(currentYear.longValue());
        InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
        inventarNaInventuri.setInventar(inventar);
        if(inventar == null){
            model.addAttribute("error2", "Skenirani inventar nije zaveden u bazi!");
            return "inventura/provodenjeInventure";
        } else if (inventarNaInventuriService.checkIfInventarAlreadyScanned(inventarNaInventuri.getInventar().getInventarniBroj(), currentYear.longValue())!=0) {
            model.addAttribute("error2", "Inventar je već skeniran!");
            return "inventura/provodenjeInventure";
        } else{
            if(inventura == null) {
                inventura = new Inventura();
                inventura.setIdInventure(currentYear.longValue());
                inventarNaInventuri.setInventura(inventura);
                inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                System.out.println(inventar);
                System.out.println(currentYear.longValue());
                System.out.println(inventura.getIdInventure());
                inventarNaInventuriService.save(inventarNaInventuri);
            }
            else{
                inventarNaInventuri.setInventura(inventura);
                inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                inventarNaInventuriService.save(inventarNaInventuri);
            }
        }
        return "redirect:/provodenjeInventure/all";
    }

    @GetMapping("delete/{idSkeniranja}")
    public String deleteById(@PathVariable(value = "idSkeniranja")Long idSkeniranja){
        inventarNaInventuriService.deleteById(idSkeniranja);
        return "redirect:/provodenjeInventure/all";
    }

    @RequestMapping("/find")
    public String findByInventarniBroj(@RequestParam("inventarniBroj")String inventarniBroj, Model model){
        List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.findByInventarniBroj(inventarniBroj);
        if (inventarNaInventuriList.isEmpty()){
            model.addAttribute("error", "Inventar pod tim brojem nije skeniran!");
            model.addAttribute("savInvNaInventuri", inventarNaInventuriService.getAllInventarNaInventuri());
            InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
            model.addAttribute("invNaInv", inventarNaInventuri);
        } else {
            model.addAttribute("savInvNaInventuri", inventarNaInventuriList);
            InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
            model.addAttribute("invNaInv", inventarNaInventuri);
        }
        return "inventura/provodenjeInventure";
    }

    @GetMapping("/findByGodinaInventure")
    public String showInventarByGodinaInventure(@RequestParam("idInventure") Long idInventure, Model model) {
        List<Inventura> allInventura = inventuraService.getAllInventura();
        List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.finbByGodinaInventure(idInventure);
        model.addAttribute("allInventura", allInventura);
        model.addAttribute("savInvNaInventuri", inventarNaInventuriList);
        InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
        model.addAttribute("invNaInv", inventarNaInventuri);
        return "inventura/provodenjeInventure";
    }
}
