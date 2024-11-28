package com.vranic.zavrsnirad.controller;

import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.*;
import com.vranic.zavrsnirad.service.*;
import com.vranic.zavrsnirad.util.BarcodeImageUtils;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/inventarIT")
public class InventarITController {
    @Autowired
    private InventarService inventarService;

    @Autowired
    private VrstaUredajaService vrstaUredajaService;

    @Autowired
    private LokacijaService lokacijaService;

    @Autowired
    private KorisnikService korisnikService;

    @Autowired
    private DobavljacService dobavljacService;

    @Autowired
    private RacunService racunService;

    @Autowired
    private RazduzenjeService razduzenjeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    public LocalDate getToday() {
        return LocalDate.now();
    }
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "admin/inventarIT";
        } else {
            // User has user role
            return "inventar/inventarIT";
        }
    }

    @GetMapping("/all")
    public String getAllItInventar(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInventar", inventarService.getInventarForIT());
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/{inventarniBroj}")
    public Inventar getInventarById(@PathVariable String inventarniBroj) {
        return inventarService.getInventarById(inventarniBroj);
    }

    @GetMapping("/update/{inventarniBroj}")
    public String updateInventar(@PathVariable(value = "inventarniBroj") String inventarniBroj, Model model) throws Exception {
        Inventar inventar = inventarService.getInventarById(inventarniBroj);
        VrstaUredaja selectedVrstaUredaja = inventar.getVrstaUredaja();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        Lokacija selectedLokacija = inventar.getLokacija();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        Korisnik selectedKorisnik = inventar.getKorisnik();
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        Dobavljac selectedDobavljac = inventar.getDobavljac();
        List<Dobavljac> allDobavljac = dobavljacService.getAllDobavljaci();
        Racun selectedRacun = inventar.getRacun();
        List<Racun> allRacun = racunService.getAllRacunForDropdown();
        Company selectedCompany = inventar.getCompany();
        List<Company> allCompany = companyService.getAllCompany();

        model.addAttribute("inventar", inventar);
        model.addAttribute("selectedVrstaUredaja", selectedVrstaUredaja);
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        model.addAttribute("selectedLokacija", selectedLokacija);
        model.addAttribute("allLokacija", allLokacija);
        model.addAttribute("selectedKorisnik", selectedKorisnik);
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("selectedDobavljac", selectedDobavljac);
        model.addAttribute("allDobavljac", allDobavljac);
        model.addAttribute("selectedRacun", selectedRacun);
        model.addAttribute("allRacun", allRacun);
        model.addAttribute("selectedCompany", selectedCompany);
        model.addAttribute("allCompany", allCompany);
        return "inventar/updateInventarIT";
    }

    @GetMapping("/addNew")
    public String addNewInventar(Model model) throws Exception{
        Inventar inventar = new Inventar();
        model.addAttribute("inventar", inventar);
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        List<Racun> allRacun = racunService.getAllRacunForDropdown();
        model.addAttribute("allRacun", allRacun);
        List<Dobavljac> allDobavljac = dobavljacService.getAllDobavljaci();
        model.addAttribute("allDobavljac", allDobavljac);
        List<Company> allCompany = companyService.getAllCompany();
        model.addAttribute("allCompany", allCompany);
        return "inventar/newInventarIT";
    }

    @PostMapping("/addNew")
    public String addInventar(@RequestParam("type") String type, @ModelAttribute("inventar") Inventar inventar, Model model) throws Exception {
        if (inventarService.checkIfInvBrojIsAvailable(inventar.getInventarniBroj()) != 0) {
            model.addAttribute("error", "Inventarni broj već postoji!");
            return "inventar/newInventarIT";
        } else {
            // Set the username to null if it is blank
            if (StringUtils.isBlank(inventar.getKorisnik().getUsername())) {
                inventar.setKorisnik(null);
            }
            //set Racun to null if it is already null
            if (inventar.getRacun() == null || inventar.getRacun().getIdRacuna() == null) {
                inventar.setRacun(null);
            }
            if(type.equals("IT")){
                String inputedInventarniBroj = inventarService.getLastInventarniBrojIT();

                // Remove the first two characters
                String prefix = inputedInventarniBroj.substring(0, 2); // Save the first two characters
                String removedPrefixInvBr = inputedInventarniBroj.substring(2);

                // Convert the remaining part to Long
                Long numberInventarniBroj = Long.valueOf(removedPrefixInvBr);

                // Increment the number
                Long nextInvBr = numberInventarniBroj + 1;

                // Convert the incremented number back to a string
                String incrementedInvBrStr = nextInvBr.toString();

                // Add leading zero if the incremented number's length is smaller than the original remaining part
                if (incrementedInvBrStr.length() < removedPrefixInvBr.length()) {
                    incrementedInvBrStr = "0" + incrementedInvBrStr;
                }

                // Concatenate the prefix and the incremented number
                String finalInventarniBroj = prefix + incrementedInvBrStr;

                System.out.println(getToday() + "\n");
                System.out.println("Dodan sljedeći inventarni broj: " + finalInventarniBroj);

                inventar.setInventarniBroj(finalInventarniBroj);
                inventarService.save(inventar);
            } else if (type.equals("LS")){
                String inputedInventarniBroj = inventarService.getLastInventarniBrojLS();

                // Remove the first two characters
                String prefix = inputedInventarniBroj.substring(0, 2); // Save the first two characters
                String removedPrefixInvBr = inputedInventarniBroj.substring(2);

                // Convert the remaining part to Long
                Long numberInventarniBroj = Long.valueOf(removedPrefixInvBr);

                // Increment the number
                Long nextInvBr = numberInventarniBroj + 1;

                // Convert the incremented number back to a string
                String incrementedInvBrStr = nextInvBr.toString();

                // Add leading zero if the incremented number's length is smaller than the original remaining part
                if (incrementedInvBrStr.length() < removedPrefixInvBr.length()) {
                    incrementedInvBrStr = "0" + incrementedInvBrStr;
                }

                // Concatenate the prefix and the incremented number
                String finalInventarniBroj = prefix + incrementedInvBrStr;

                System.out.println(getToday() + "\n");
                System.out.println("Dodan sljedeći inventarni broj: " + finalInventarniBroj);

                inventar.setInventarniBroj(finalInventarniBroj);
                inventarService.save(inventar);
            } else if (type.equals("SI")){
                String inputedInventarniBroj = inventarService.getLastInventarniBrojSI();

                // Remove the first two characters
                String prefix = inputedInventarniBroj.substring(0, 2); // Save the first two characters
                String removedPrefixInvBr = inputedInventarniBroj.substring(2);

                // Convert the remaining part to Long
                Long numberInventarniBroj = Long.valueOf(removedPrefixInvBr);

                // Increment the number
                Long nextInvBr = numberInventarniBroj + 1;

                // Convert the incremented number back to a string
                String incrementedInvBrStr = nextInvBr.toString();

                // Add leading zero if the incremented number's length is smaller than the original remaining part
                if (incrementedInvBrStr.length() < removedPrefixInvBr.length()) {
                    incrementedInvBrStr = "0" + incrementedInvBrStr;
                }

                // Concatenate the prefix and the incremented number
                String finalInventarniBroj = prefix + incrementedInvBrStr;

                System.out.println(getToday() + "\n");
                System.out.println("Dodan sljedeći inventarni broj: " + finalInventarniBroj);

                inventar.setInventarniBroj(finalInventarniBroj);
                inventarService.save(inventar);
            } else {
                System.out.println(getToday() + "\n");
                System.out.println("Dodan sljedeći inventarni broj: " + inventar.getInventarniBroj());
                inventarService.save(inventar);
            }
        }
        return "redirect:/inventarIT/all";
    }

    @PostMapping("/save")
    public String saveInventar(@ModelAttribute("inventar") Inventar inventar) throws Exception {
        // Set the username to null if it is blank
        if (StringUtils.isBlank(inventar.getKorisnik().getUsername())) {
            inventar.setKorisnik(null);
        }
        //set Racun to null if it is already null
        if (inventar.getRacun() == null || inventar.getRacun().getIdRacuna() == null) {
            inventar.setRacun(null);
        }
        inventarService.save(inventar);
        return "redirect:/inventarIT/all";
    }

    @GetMapping("delete/{inventarniBroj}")
    public String deleteById(@PathVariable(value = "inventarniBroj") String inventarniBroj) {
        inventarService.deleteById(inventarniBroj);
        return "redirect:/inventarIT/all";
    }

    @GetMapping("/find")
    public String findInventarByName(@RequestParam("inventarniBroj") String inventarniBroj, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventar> inventarList = inventarService.findInventarByInvBroj(inventarniBroj);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        if (inventarList.isEmpty()) {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("error", "Inventar pod tim brojem ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getAllInventar());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findBySerialNumber")
    public String findInventarBySerialNumber(@RequestParam("serialNumber") String serijskiBroj, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventar> inventarList = inventarService.getInventarBySerialNumber(serijskiBroj);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        if (inventarList.isEmpty()) {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("error2", "Inventar tog serijskog broja ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getAllInventar());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findByMacAddress")
    public String findInventarByMacAddress(@RequestParam("addressMac") String addressMac, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventar> inventarList = inventarService.getInventarByMacAddress(addressMac);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        if (inventarList.isEmpty()) {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("error4", "Inventar tražene MAC adrese ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getAllInventar());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findByUser")
    public String showInventarByUser(@RequestParam("lastName") String lastName, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventar> inventarList = inventarService.getInventarByUser(lastName);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        if (inventarList.isEmpty()) {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("error3", "Korisnik traženog prezimena ne zadužuje inventar!");
            model.addAttribute("savInventar", inventarService.getAllInventar());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("zaduzi/{inventarniBroj}")
    public String zaduziInventar(@PathVariable(value = "inventarniBroj") String inventarniBroj, Model model) throws Exception {
        Inventar inventar = inventarService.getInventarById(inventarniBroj);
        VrstaUredaja selectedVrstaUredaja = inventar.getVrstaUredaja();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        Lokacija selectedLokacija = inventar.getLokacija();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        Korisnik selectedKorisnik = inventar.getKorisnik();
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        Dobavljac selectedDobavljac = inventar.getDobavljac();
        List<Dobavljac> allDobavljac = dobavljacService.getAllDobavljaci();
        Racun selectedRacun = inventar.getRacun();
        List<Racun> allRacun = racunService.getAllRacunForDropdown();

        model.addAttribute("inventar", inventar);
        model.addAttribute("selectedVrstaUredaja", selectedVrstaUredaja);
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        model.addAttribute("selectedLokacija", selectedLokacija);
        model.addAttribute("allLokacija", allLokacija);
        model.addAttribute("selectedKorisnik", selectedKorisnik);
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("selectedDobavljac", selectedDobavljac);
        model.addAttribute("allDobavljac", allDobavljac);
        model.addAttribute("selectedRacun", selectedRacun);
        model.addAttribute("allRacun", allRacun);
        return "inventar/zaduziInventarIT";
    }

    @PostMapping("/saveZaduzenje")
    @Transactional
    public String zaduzenjeInventara(@ModelAttribute("inventar") Inventar inventar) throws Exception {
        Integer idVrste = inventar.getVrstaUredaja().getIdVrsteUredaja().intValue();
        if(idVrste.equals(1)||idVrste.equals(6)){
            //split string nazivUredaja by one or more spaces and -
            String [] nazivUredaja = inventar.getNazivUredaja().split("\\s+|-");
            String suffix ="";
            if(inventar.getNazivUredaja().contains("G6")){
                suffix="G6";
            } else if (inventar.getNazivUredaja().contains("G7")){
                suffix="G7";
            } else if (inventar.getNazivUredaja().contains("G8")){
                suffix="G8";
            } else if (inventar.getNazivUredaja().contains("G9")){
                suffix="G9";
            }
            String firstPartOfHostname = nazivUredaja[0];
            String secondPartOfHostname = inventar.getKorisnik().getUsername();
            String hostname = firstPartOfHostname + suffix + "-" + secondPartOfHostname;
            if(inventar.getDatumZaduzenja()!=null)
            {
                inventarService.zaduziInventar(hostname, inventar.getLokacija().getIdLokacije(), inventar.getKorisnik().getUsername(),
                        inventar.getDatumZaduzenja(), inventar.getInventarniBroj());

            } else {
                LocalDate datumZaduzenja = getToday();
                System.out.println(getToday());
                inventarService.zaduziInventar(hostname, inventar.getLokacija().getIdLokacije(), inventar.getKorisnik().getUsername(),
                        datumZaduzenja, inventar.getInventarniBroj());
            }
        }else {
            if(inventar.getDatumZaduzenja()!=null)
            {
                inventarService.zaduziInventar(inventar.getHostname(), inventar.getLokacija().getIdLokacije(), inventar.getKorisnik().getUsername(),
                        inventar.getDatumZaduzenja(), inventar.getInventarniBroj());

            } else {
                LocalDate datumZaduzenja = getToday();
                System.out.println(getToday());
                inventarService.zaduziInventar(inventar.getHostname(), inventar.getLokacija().getIdLokacije(), inventar.getKorisnik().getUsername(),
                        datumZaduzenja, inventar.getInventarniBroj());
            }
        }
        return "redirect:/inventarIT/all";
    }

    @GetMapping("razduzi/{inventarniBroj}")
    @Transactional
    public String razduziInventar(@PathVariable(value = "inventarniBroj") String inventarniBroj) throws Exception{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        Inventar inventar = inventarService.getInventarById(inventarniBroj);
        Razduzenje razduzenje = new Razduzenje();
        razduzenje.setInventar(inventar);
        razduzenje.setHostname(inventar.getHostname());
        razduzenje.setLokacija(inventar.getLokacija());
        razduzenje.setKorisnik(inventar.getKorisnik());
        razduzenje.setDatumZaduzenja(inventar.getDatumZaduzenja());
        razduzenje.setDatumRazduzenja(getToday());
        razduzenje.setUser(user);
        razduzenje.setNapomena(inventar.getNapomena());
        razduzenjeService.save(razduzenje);
        inventarService.razduziInventar(getToday(), inventarniBroj);
        return "redirect:/inventarIT/all";
    }

    @GetMapping("/findByVrsta")
    public String showInventarByVrstaUredaja(@RequestParam("idVrsteUredaja") Long idVrsteUredaja, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        List<Inventar> inventarList = inventarService.getInventarByVrstaUredaja(idVrsteUredaja);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        model.addAttribute("savInventar", inventarList);
        Inventar inventar = new Inventar();
        model.addAttribute("inventar", inventar);
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findByLokacija")
    public String showInventarByLokacija(@RequestParam("idLokacije") Long idLokacije, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<Inventar> inventarList = inventarService.getInventarByLokacija(idLokacije);
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        model.addAttribute("allLokacija", allLokacija);
        model.addAttribute("savInventar", inventarList);
        Inventar inventar = new Inventar();
        model.addAttribute("inventar", inventar);
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findByTipInventara")
    public String showInventarByTipInventara(@RequestParam("tipInventara") String tipInventara, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(tipInventara.equals("OS"))
        {
            List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
            List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
            List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("allKorisnik", allKorisnik);
            List<Inventar> inventarList = inventarService.getInventarByOS();
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
            return getViewBasedOnRole(auth);
        } else if(tipInventara.equals("IT")) {
            List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
            List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
            List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("allKorisnik", allKorisnik);
            List<Inventar> inventarList = inventarService.getInventarByIT();
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
            return getViewBasedOnRole(auth);
        } else if(tipInventara.equals("LS")) {
            List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
            List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
            List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("allKorisnik", allKorisnik);
            List<Inventar> inventarList = inventarService.getInventarByLS();
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
            return getViewBasedOnRole(auth);
        } else {
            List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
            List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
            List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("allKorisnik", allKorisnik);
            List<Inventar> inventarList = inventarService.getInventarBySI();
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
            return getViewBasedOnRole(auth);
        }
    }

    @GetMapping(value = "/ean13/{inventarniBroj}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateAndDownloadEAN13Barcode(@PathVariable(value = "inventarniBroj") String inventarniBroj) throws Exception {
            String originalInventarniBroj = inventarniBroj;
            if(originalInventarniBroj.length()<2){
                String sitniInventar = "";
                if(sitniInventar.equals("SI"))
                {
                    inventarniBroj = inventarniBroj.substring(2);
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "2" + inventarniBroj.substring(1);
                } else if (sitniInventar.equals("IT")) {
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "3" + inventarniBroj.substring(1);
                } else if (sitniInventar.equals("LS")) {
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "4" + inventarniBroj.substring(1);
                } else {
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "1" + inventarniBroj.substring(1);
                }
                System.out.println(inventarniBroj);
            }
            else {
                String sitniInventar = originalInventarniBroj.substring(0,2);
                if(sitniInventar.equals("SI"))
                {
                    inventarniBroj = inventarniBroj.substring(2);
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "2" + inventarniBroj.substring(1);
                } else if (sitniInventar.equals("IT")) {
                    inventarniBroj = inventarniBroj.substring(2);
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "3" + inventarniBroj.substring(1);
                } else if (sitniInventar.equals("LS")) {
                    inventarniBroj = inventarniBroj.substring(2);
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "4" + inventarniBroj.substring(1);
                } else {
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "1" + inventarniBroj.substring(1);
                }
                System.out.println(inventarniBroj);
            }
            System.out.println(inventarniBroj);
            BitMatrix bitMatrix = BarcodeGeneratorService.generateEAN13Barcode(inventarniBroj);
            byte[] barcodeImage = BarcodeImageUtils.convertBitMatrixToByteArray(bitMatrix);

            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(barcodeImage));
            int combinedHeight = bufferedImage.getHeight() + 60;
            BufferedImage combinedImage = new BufferedImage(bufferedImage.getWidth(), combinedHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics g = combinedImage.getGraphics();
            g.setColor(Color.WHITE); // Set the text color to white
            g.fillRect(0, 0, bufferedImage.getWidth(), combinedHeight); // Add a white background above the barcode
            g.setColor(Color.BLACK); // Set the text color back to black
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20)); //set font for text on barcode

            // Draw "AITAC d.o.o." above the barcode
            g.drawString("AITAC d.o.o.", 85, 25);

            // Draw the barcode image
            g.drawImage(bufferedImage, 0, 30, null);

            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 30)); //set font for text on barcode
            // Draw the EAN-13 code text below the barcode
            g.drawString(inventarniBroj, 50, bufferedImage.getHeight() + 55);

            ByteArrayOutputStream combinedOutputStream = new ByteArrayOutputStream();
            ImageIO.write(combinedImage, "png", combinedOutputStream);
            byte[] combinedImageByteArray = combinedOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("barcode"+originalInventarniBroj+".png").build());
            return new ResponseEntity<>(combinedImageByteArray, headers, HttpStatus.OK);
    }

    @GetMapping("/generatePDF")
    public ResponseEntity<byte[]> generatePDF(/*HttpServletResponse response*/) throws Exception {
        // Create a new PDF document
        Document document = new Document(PageSize.A4.rotate());

        // Create a PdfWriter instance to write the document to the response output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        // Open the document
        document.open();

        //Paths to arial fonts
        String arialBold = "/static/fonts/arialbd.ttf";
        String arialNormal = "/static/fonts/arial.ttf";
        String arialBoldItalic = "/static/fonts/arialbi.ttf";

        // Set the font for Croatian characters
        // Create the font using BaseFont.createFont
        BaseFont arialBoldFont = BaseFont.createFont(arialBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont arialBoldItalicFont = BaseFont.createFont(arialBoldItalic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font croatianFont = FontFactory.getFont(arialNormal, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Set image and it's size
        String imagePath2 = "static/images/AitacLogoBlueBackgroundHiRes.jpg"; // Relative path to the image file
        Resource resource2 = new ClassPathResource(imagePath2);
        Image image2 = Image.getInstance(resource2.getURL());
        float desiredWidthInCm2 = 5f;
        float desiredHeightInCm2 = 2f;

        // Convert centimeters to points
        float desiredWidthInPoints2 = desiredWidthInCm2 * 72 / 2.54f;
        float desiredHeightInPoints2 = desiredHeightInCm2 * 72 / 2.54f;

        // Set the desired width and height of the image in points
        float desiredWidth2 = desiredWidthInPoints2;
        float desiredHeight2 = desiredHeightInPoints2;
        image2.scaleToFit(desiredWidth2, desiredHeight2);
        float pageWidth = document.getPageSize().getWidth();
        float y2 = document.getPageSize().getHeight() - image2.getScaledHeight() - 0.5f * 72 / 2.54f; // Position from the bottom
        image2.setAbsolutePosition(0.5f * 72 / 2.54f, y2);
        document.add(image2);

        Paragraph header = new Paragraph();
        Font boldFont = new Font(arialBoldItalicFont, 18, Font.BOLD);
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O OPREMI", boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20); // Adjust the value as per your requirement
        document.add(header);

        //Adding date of the report
        Paragraph printDate = new Paragraph();
        String todayDate = "Datum izvještaja: " + getToday();
        Font dateFont = new Font(arialBoldItalicFont, 10, Font.ITALIC);
        Phrase datePhrase = new Phrase(todayDate, dateFont);
        printDate.add(datePhrase);
        printDate.setAlignment(Element.ALIGN_LEFT);
        printDate.setSpacingAfter(5);
        document.add(printDate);

        // Create a table with 15 columns
        PdfPTable table = new PdfPTable(15);

        // Set the table width as a percentage of the available page width
        table.setWidthPercentage(100);

        // Set the data cell style
        PdfPCell dataCell = new PdfPCell();
        dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Set table header cell styles
        Font headerFont = new Font(arialBoldFont, 12, Font.BOLD);
        headerFont.setColor(BaseColor.WHITE);
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(5);

        // Add table header cells
        setCellContentAndFont(headerCell,"Inventarni broj", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Naziv uređaja", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Serijski broj", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Vrsta uređaja", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Hostname", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Lokacija", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Username", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"LAN MAC", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"WiFi MAC", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Datum zaduženja", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Datum razduženja", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Istek garancije", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Račun/Dokument", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Dobavljač", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Napomena", headerFont);
        table.addCell(headerCell);

        // Get the list of Inventar objects from service
        List<Inventar> inventari = inventarService.getInventarForIT();

        // Add data cells to the table
        for (Inventar inventar : inventari) {
            PdfPCell cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            setCellContentAndFont(cell, inventar.getInventarniBroj(), croatianFont);
            table.addCell(cell);
            setCellContentAndFont(cell, inventar.getNazivUredaja(), croatianFont);
            table.addCell(cell);
            if(inventar.getNazivUredaja() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getSerijskiBroj(), croatianFont);
            }
            table.addCell(cell);
            setCellContentAndFont(cell, inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont);
            table.addCell(cell);
            if(inventar.getHostname() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getHostname(), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getLokacija() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getLokacija().getNazivLokacije(), croatianFont);
            }
            table.addCell(cell);
            Korisnik korisnik = inventar.getKorisnik();
            if(korisnik != null && korisnik.getUsername() != null){
                setCellContentAndFont(cell, korisnik.getUsername(), croatianFont);
            } else {
                setCellContentAndFont(cell, "", croatianFont);

            }
            table.addCell(cell);
            if(inventar.getLanMac() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getLanMac(), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getWifiMac() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getWifiMac(), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getDatumZaduzenja() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, String.valueOf(inventar.getDatumZaduzenja()), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getDatumRazduzenja() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, String.valueOf(inventar.getDatumRazduzenja()), croatianFont);
            }
            table.addCell(cell);
            setCellContentAndFont(cell, String.valueOf(inventar.getWarrantyEnding()), croatianFont);
            table.addCell(cell);
            if(inventar.getRacun() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getRacun().getBrojRacuna(), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getDobavljac() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getDobavljac().getNazivDobavljaca(), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getNapomena() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getNapomena(), croatianFont);
            }
            table.addCell(cell);
        }

        // Add the table to the document
        document.add(table);

        //Adding another image and setting its size and position
        String imagePath = "static/images/AitacLine.png"; // Relative path to the image file
        Resource resource = new ClassPathResource(imagePath);
        Image image = Image.getInstance(resource.getURL());

        // Set the desired width and height of the image in centimeters
        float desiredWidthInCm = 17f;
        float desiredHeightInCm = 7f;

        // Convert centimeters to points
        float desiredWidthInPoints = desiredWidthInCm * 72 / 2.54f;
        float desiredHeightInPoints = desiredHeightInCm * 72 / 2.54f;

        // Set the desired width and height of the image in points
        float desiredWidth = desiredWidthInPoints;
        float desiredHeight = desiredHeightInPoints;
        image.scaleToFit(desiredWidth, desiredHeight);

        // Calculate the coordinates to position the image at the bottom
        float x = (pageWidth - desiredWidth) / 2; // Centered horizontally
        float y = image.getScaledHeight() + document.bottomMargin(); // Position from the bottom

        image.setAbsolutePosition(x, y);
        document.add(image);
        // Close the document
        document.close();

        byte[] pdfContent = baos.toByteArray();
        // Set HTTP headers for response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("InventarIT-izvjestaj.pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/printPDFzaduzenja")
    public ResponseEntity<byte[]> printPDFzaduzenja(@RequestParam("selectedItems") List<String> selectedItems/*, HttpServletResponse response*/) throws Exception {
        // Get the list of Inventar objects from service
        List<Inventar> selectedInventar = selectedItems.stream()
                .map(inventarniBroj -> inventarService.getInventarById(inventarniBroj))
                .filter(inventar -> inventar != null)
                .toList();

        Inventar inventarOne = selectedInventar.get(0);

        String filename = String.join("_", selectedItems);

        // Create a new PDF document
        Document document = new Document(PageSize.A4);

        // Create a PdfWriter instance to write the document to the response output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        // Open the document
        document.open();

        //Paths to arial fonts
        String arialBold = "/static/fonts/arialbd.ttf";
        String arialNormal = "/static/fonts/arial.ttf";
        String arialBoldItalic = "/static/fonts/arialbi.ttf";

        // Set the font for Croatian characters
        // Create the font using BaseFont.createFont
        BaseFont arialBoldFont = BaseFont.createFont(arialBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont arialNormalFont = BaseFont.createFont(arialNormal, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont arialBoldItalicFont = BaseFont.createFont(arialBoldItalic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font croatianFont = FontFactory.getFont(arialNormal, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font croatianFontBold = FontFactory.getFont(arialBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Set image and it's size
        String imagePath2 = "static/images/AITACvaluesChallengesSolutions.png"; // Relative path to the image file
        Resource resource2 = new ClassPathResource(imagePath2);
        Image image2 = Image.getInstance(resource2.getURL());
        float desiredWidthInCm2 = 5f;
        float desiredHeightInCm2 = 2f;

        // Convert centimeters to points
        float desiredWidthInPoints2 = desiredWidthInCm2 * 72 / 2.54f;
        float desiredHeightInPoints2 = desiredHeightInCm2 * 72 / 2.54f;

        // Set the desired width and height of the image in points
        float desiredWidth2 = desiredWidthInPoints2;
        float desiredHeight2 = desiredHeightInPoints2;
        image2.scaleToFit(desiredWidth2, desiredHeight2);
        float pageWidth = document.getPageSize().getWidth();
        float y2 = document.getPageSize().getHeight() - image2.getScaledHeight() - 0.5f * 72 / 2.54f; // Position from the bottom
        image2.setAbsolutePosition(1.5f * 72 / 2.54f, y2);
        document.add(image2);

        String imagePath3 = "static/images/AitacData.png"; // Relative path to the image file
        Resource resource3 = new ClassPathResource(imagePath3);
        Image image3 = Image.getInstance(resource3.getURL());

        float desiredWidthInPoints3 = desiredWidthInCm2 * 72 / 2.54f;
        float desiredHeightInPoints3 = desiredHeightInCm2 * 72 / 2.54f;

        // Set the desired width and height of the image in points
        float desiredWidth3 = desiredWidthInPoints3;
        float desiredHeight3 = desiredHeightInPoints3;
        image3.scaleToFit(desiredWidth3, desiredHeight3);
        float x3 = pageWidth - (1.0f * 72 / 2.54f) - image3.getScaledWidth();
        float y3 = y2 + image2.getScaledHeight() - image3.getScaledHeight(); // Adjusted to align top of image2 with top of image3
        image3.setAbsolutePosition(x3, y3);
        image3.setSpacingAfter(40);
        document.add(image3);

        if(inventarOne.getCompany().getIdCompany()==1) {

            // Add Image 4 below the other images and center it
            String imagePath4 = "static/images/headerPrintoutAitacDoo.png"; // Relative path to the image file
            Resource resource4 = new ClassPathResource(imagePath4);
            Image image4 = Image.getInstance(resource4.getURL());

            // Set desired dimensions in cm (example: adjust as per your requirement)
            float desiredWidthInCm4 = 19f; // Example width
            float desiredHeightInCm4 = 5f; // Example height

            // Convert centimeters to points
            float desiredWidthInPoints4 = desiredWidthInCm4 * 72 / 2.54f;
            float desiredHeightInPoints4 = desiredHeightInCm4 * 72 / 2.54f;

            // Scale image to fit the desired dimensions
            image4.scaleToFit(desiredWidthInPoints4, desiredHeightInPoints4);

            // Calculate x and y positions to center the image on the page
            float x4 = (pageWidth - image4.getScaledWidth()) / 2; // Center horizontally
            float y4 = y3 - image3.getScaledHeight(); // Position below image3 with a margin

            image4.setAbsolutePosition(x4, y4);
            document.add(image4);

            Paragraph header = new Paragraph();
            Font boldFont = new Font(arialBoldItalicFont, 18, Font.BOLD);
            Phrase headerPhrase = new Phrase("Zaduženja tijekom rada", boldFont);
            header.add(headerPhrase);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(75);
            header.setSpacingBefore(50);
            document.add(header);

            Paragraph podaciKorisnika = new Paragraph();
            Font fontImePrezime = new Font(arialNormalFont, 9, Font.NORMAL);
            Phrase imePrezime = new Phrase("Ime i Prezime: ", fontImePrezime);
            podaciKorisnika.add(imePrezime);
            podaciKorisnika.setAlignment(Element.ALIGN_LEFT);
            podaciKorisnika.setSpacingAfter(5);
            Font boldFontKorisnika = new Font(arialBoldFont, 13, Font.BOLD);
            Chunk nazivKorisnika = new Chunk(inventarOne.getKorisnik().getFirstName() + " " + inventarOne.getKorisnik().getLastName(), boldFontKorisnika);
            nazivKorisnika.setUnderline(0.3f, -2f);
            Phrase selectedKorisnik = new Phrase(nazivKorisnika);
            podaciKorisnika.add(selectedKorisnik);
            document.add(podaciKorisnika);

            //Adding date of the report
            Paragraph printDate = new Paragraph();
            String datum = "Datum: ";
            String todayDate = getToday().format(formatter);
            Font datumFont = new Font(arialNormalFont, 9, Font.NORMAL);
            Font dateFont = new Font(arialBoldFont, 10, Font.BOLD);
            Phrase datumPhrase = new Phrase(datum, datumFont);
            Phrase datePhrase = new Phrase(todayDate, dateFont);
            printDate.add(datumPhrase);
            printDate.add(datePhrase);
            printDate.setAlignment(Element.ALIGN_LEFT);
            printDate.setSpacingAfter(70);
            document.add(printDate);

            // Create a table with 15 columns
            PdfPTable table = new PdfPTable(7);

            // Set the table width as a percentage of the available page width
            table.setWidthPercentage(100);

            float firstColumnWidthPercentage = 5f; // Primjer postotka širine prvog stupca
            float remainingWidthPercentage = (100f - firstColumnWidthPercentage) / 6; // Preostali postotak za preostalih 6 stupaca
            float[] columnWidths = {firstColumnWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage};
            table.setWidths(columnWidths);

            // Set the data cell style
            PdfPCell dataCell = new PdfPCell();
            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Set table header cell styles
            Font headerFont = new Font(arialBoldFont, 12, Font.BOLD);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(5);

            // Add table header cells
            setCellContentAndFont(headerCell, "", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "INVENTARNI \n BROJ", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "NAZIV \n UREĐAJA", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "SERIJSKI \n BROJ", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "VRSTA \n UREĐAJA", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "DATUM \n ZADUŽENJA", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "DATUM \n RAZDUŽENJA", headerFont);
            table.addCell(headerCell);

            int counter = 1;
            // Add data cells to the table
            for (Inventar inventar : selectedInventar) {
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);


                setCellContentAndFont(cell, String.valueOf(counter), croatianFont);
                table.addCell(cell);
                counter++;
                setCellContentAndFont(cell, inventar.getInventarniBroj(), croatianFont);
                table.addCell(cell);
                setCellContentAndFont(cell, inventar.getNazivUredaja(), croatianFont);
                table.addCell(cell);
                if (inventar.getNazivUredaja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, inventar.getSerijskiBroj(), croatianFont);
                }
                table.addCell(cell);
                setCellContentAndFont(cell, inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont);
                table.addCell(cell);
                if (inventar.getDatumZaduzenja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, inventar.getDatumZaduzenja().format(formatter), croatianFontBold);
                }
                table.addCell(cell);
                if (inventar.getDatumRazduzenja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, inventar.getDatumRazduzenja().format(formatter), croatianFont);
                }
                table.addCell(cell);
            }

            // Add the table to the document
            document.add(table);

            Paragraph pravilnik = new Paragraph();
            Font fontPravilnik = new Font(arialNormalFont, 9, Font.NORMAL);
            Font fontPravilnikBold = new Font(arialBoldFont, 9, Font.BOLD);
            Font fontPravilnikLink = new Font(arialNormalFont, 9, Font.BOLD);
            fontPravilnikLink.setColor(35, 47, 131);
            Phrase redI = new Phrase();
            redI.add(new Chunk("Svojim potpisom korisnik potvrđuje da je upoznat te da će se pridržavati pravilnika o " +
                    "korištenju informatičke opreme: \n", fontPravilnik));
            Chunk boldChunk = new Chunk("QMS-POL-DOC-001-2  Politika pravilne uporabe informacijskih resursa \n", fontPravilnikBold);
            redI.add(boldChunk);
            redI.add(new Chunk("LINK: ", fontPravilnik));
            Chunk linkChunk = new Chunk("\\\\osiris\\ISO\\AITAC ISO 9001\\15. POSLOVNE POLITIKE \n", fontPravilnikLink);
            linkChunk.setUnderline(0.1f, -2f);
            redI.add(linkChunk);
            redI.add(new Chunk("\n"));
            Phrase redII = new Phrase("Svojim potpisom potvrđujem da je sve navedeno zaduženo u dobrom stanju.", fontPravilnik);
            pravilnik.setAlignment(Element.ALIGN_LEFT);
            pravilnik.add(redI);
            pravilnik.add(redII);
            pravilnik.setSpacingBefore(60);
            document.add(pravilnik);

            Paragraph potpisnik = new Paragraph();
            Font fontPotpisnik = new Font(arialNormalFont, 9, Font.NORMAL);
            Phrase red1 = new Phrase();
            red1.add(new Chunk("_____________________________________", fontPotpisnik));
            red1.add(new Chunk("                              "));
            red1.add(new Chunk("_____________________________________ \n", fontPotpisnik));
            Phrase red2 = new Phrase();
            red2.add(new Chunk("Zaposlenik", fontPotpisnik));
            red2.add(new Chunk("                                                                      "));
            red2.add(new Chunk("Odgovorna Osoba", fontPotpisnik));
            potpisnik.setAlignment(Element.ALIGN_CENTER);
            potpisnik.add(red1);
            potpisnik.add(red2);
            potpisnik.setSpacingBefore(90);
            potpisnik.setSpacingAfter(20);
            document.add(potpisnik);

            //Adding another image and setting its size and position
            String imagePath = "static/images/AitacLine.png"; // Relative path to the image file
            Resource resource = new ClassPathResource(imagePath);
            Image image = Image.getInstance(resource.getURL());

            // Set the desired width and height of the image in centimeters
            float desiredWidthInCm = 17f;
            float desiredHeightInCm = 7f;

            // Convert centimeters to points
            float desiredWidthInPoints = desiredWidthInCm * 72 / 2.54f;
            float desiredHeightInPoints = desiredHeightInCm * 72 / 2.54f;

            // Set the desired width and height of the image in points
            float desiredWidth = desiredWidthInPoints;
            float desiredHeight = desiredHeightInPoints;
            image.scaleToFit(desiredWidth, desiredHeight);

            // Calculate the coordinates to position the image at the bottom
            float x = (pageWidth - desiredWidth) / 2; // Centered horizontally
            float y = document.bottomMargin() - image.getScaledHeight(); // Position from the bottom

            image.setAbsolutePosition(x, y);
            document.add(image);
            // Close the document
            document.close();

            byte[] pdfContent = baos.toByteArray();
            // Set HTTP headers for response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("Inv" + filename + ".pdf")
                    .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);

        } else if (inventarOne.getCompany().getIdCompany()==2) {

            // Add Image 4 below the other images and center it
            String imagePath4 = "static/images/headerPrintoutAitacDoo.png"; // Relative path to the image file
            Resource resource4 = new ClassPathResource(imagePath4);
            Image image4 = Image.getInstance(resource4.getURL());

            // Set desired dimensions in cm (example: adjust as per your requirement)
            float desiredWidthInCm4 = 19f; // Example width
            float desiredHeightInCm4 = 5f; // Example height

            // Convert centimeters to points
            float desiredWidthInPoints4 = desiredWidthInCm4 * 72 / 2.54f;
            float desiredHeightInPoints4 = desiredHeightInCm4 * 72 / 2.54f;

            // Scale image to fit the desired dimensions
            image4.scaleToFit(desiredWidthInPoints4, desiredHeightInPoints4);

            // Calculate x and y positions to center the image on the page
            float x4 = (pageWidth - image4.getScaledWidth()) / 2; // Center horizontally
            float y4 = y3 - image3.getScaledHeight(); // Position below image3 with a margin

            image4.setAbsolutePosition(x4, y4);
            document.add(image4);

            Paragraph header = new Paragraph();
            Font boldFont = new Font(arialBoldItalicFont, 18, Font.BOLD);
            Phrase headerPhrase = new Phrase("Zaduženja tijekom rada", boldFont);
            header.add(headerPhrase);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(75);
            header.setSpacingBefore(50);
            document.add(header);

            Paragraph podaciKorisnika = new Paragraph();
            Font fontImePrezime = new Font(arialNormalFont, 9, Font.NORMAL);
            Phrase imePrezime = new Phrase("Ime i Prezime: ", fontImePrezime);
            podaciKorisnika.add(imePrezime);
            podaciKorisnika.setAlignment(Element.ALIGN_LEFT);
            podaciKorisnika.setSpacingAfter(5);
            Font boldFontKorisnika = new Font(arialBoldFont, 13, Font.BOLD);
            Chunk nazivKorisnika = new Chunk(inventarOne.getKorisnik().getFirstName() + " " + inventarOne.getKorisnik().getLastName(), boldFontKorisnika);
            nazivKorisnika.setUnderline(0.3f, -2f);
            Phrase selectedKorisnik = new Phrase(nazivKorisnika);
            podaciKorisnika.add(selectedKorisnik);
            document.add(podaciKorisnika);

            //Adding date of the report
            Paragraph printDate = new Paragraph();
            String datum = "Datum: ";
            String todayDate = getToday().format(formatter);
            Font datumFont = new Font(arialNormalFont, 9, Font.NORMAL);
            Font dateFont = new Font(arialBoldFont, 10, Font.BOLD);
            Phrase datumPhrase = new Phrase(datum, datumFont);
            Phrase datePhrase = new Phrase(todayDate, dateFont);
            printDate.add(datumPhrase);
            printDate.add(datePhrase);
            printDate.setAlignment(Element.ALIGN_LEFT);
            printDate.setSpacingAfter(70);
            document.add(printDate);

            // Create a table with 15 columns
            PdfPTable table = new PdfPTable(7);

            // Set the table width as a percentage of the available page width
            table.setWidthPercentage(100);

            float firstColumnWidthPercentage = 5f; // Primjer postotka širine prvog stupca
            float remainingWidthPercentage = (100f - firstColumnWidthPercentage) / 6; // Preostali postotak za preostalih 6 stupaca
            float[] columnWidths = {firstColumnWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage};
            table.setWidths(columnWidths);

            // Set the data cell style
            PdfPCell dataCell = new PdfPCell();
            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Set table header cell styles
            Font headerFont = new Font(arialBoldFont, 12, Font.BOLD);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(5);

            // Add table header cells
            setCellContentAndFont(headerCell, "", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "INVENTARNI \n BROJ", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "NAZIV \n UREĐAJA", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "SERIJSKI \n BROJ", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "VRSTA \n UREĐAJA", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "DATUM \n ZADUŽENJA", headerFont);
            table.addCell(headerCell);
            setCellContentAndFont(headerCell, "DATUM \n RAZDUŽENJA", headerFont);
            table.addCell(headerCell);

            int counter = 1;
            // Add data cells to the table
            for (Inventar inventar : selectedInventar) {
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);


                setCellContentAndFont(cell, String.valueOf(counter), croatianFont);
                table.addCell(cell);
                counter++;
                setCellContentAndFont(cell, inventar.getInventarniBroj(), croatianFont);
                table.addCell(cell);
                setCellContentAndFont(cell, inventar.getNazivUredaja(), croatianFont);
                table.addCell(cell);
                if (inventar.getNazivUredaja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, inventar.getSerijskiBroj(), croatianFont);
                }
                table.addCell(cell);
                setCellContentAndFont(cell, inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont);
                table.addCell(cell);
                if (inventar.getDatumZaduzenja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, inventar.getDatumZaduzenja().format(formatter), croatianFontBold);
                }
                table.addCell(cell);
                if (inventar.getDatumRazduzenja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, inventar.getDatumRazduzenja().format(formatter), croatianFont);
                }
                table.addCell(cell);
            }

            // Add the table to the document
            document.add(table);

            Paragraph pravilnik = new Paragraph();
            Font fontPravilnik = new Font(arialNormalFont, 9, Font.NORMAL);
            Font fontPravilnikBold = new Font(arialBoldFont, 9, Font.BOLD);
            Font fontPravilnikLink = new Font(arialNormalFont, 9, Font.BOLD);
            fontPravilnikLink.setColor(35, 47, 131);
            Phrase redI = new Phrase();
            redI.add(new Chunk("Svojim potpisom korisnik potvrđuje da je upoznat te da će se pridržavati pravilnika o " +
                    "korištenju informatičke opreme: \n", fontPravilnik));
            Chunk boldChunk = new Chunk("QMS-POL-DOC-001-2  Politika pravilne uporabe informacijskih resursa \n", fontPravilnikBold);
            redI.add(boldChunk);
            redI.add(new Chunk("LINK: ", fontPravilnik));
            Chunk linkChunk = new Chunk("\\\\osiris\\ISO\\AITAC ISO 9001\\15. POSLOVNE POLITIKE \n", fontPravilnikLink);
            linkChunk.setUnderline(0.1f, -2f);
            redI.add(linkChunk);
            redI.add(new Chunk("\n"));
            Phrase redII = new Phrase("Svojim potpisom potvrđujem da je sve navedeno zaduženo u dobrom stanju.", fontPravilnik);
            pravilnik.setAlignment(Element.ALIGN_LEFT);
            pravilnik.add(redI);
            pravilnik.add(redII);
            pravilnik.setSpacingBefore(60);
            document.add(pravilnik);

            Paragraph potpisnik = new Paragraph();
            Font fontPotpisnik = new Font(arialNormalFont, 9, Font.NORMAL);
            Phrase red1 = new Phrase();
            red1.add(new Chunk("_____________________________________", fontPotpisnik));
            red1.add(new Chunk("                              "));
            red1.add(new Chunk("_____________________________________ \n", fontPotpisnik));
            Phrase red2 = new Phrase();
            red2.add(new Chunk("Zaposlenik", fontPotpisnik));
            red2.add(new Chunk("                                                                      "));
            red2.add(new Chunk("Odgovorna Osoba", fontPotpisnik));
            potpisnik.setAlignment(Element.ALIGN_CENTER);
            potpisnik.add(red1);
            potpisnik.add(red2);
            potpisnik.setSpacingBefore(90);
            potpisnik.setSpacingAfter(20);
            document.add(potpisnik);

            //Adding another image and setting its size and position
            String imagePath = "static/images/AitacLine.png"; // Relative path to the image file
            Resource resource = new ClassPathResource(imagePath);
            Image image = Image.getInstance(resource.getURL());

            // Set the desired width and height of the image in centimeters
            float desiredWidthInCm = 17f;
            float desiredHeightInCm = 7f;

            // Convert centimeters to points
            float desiredWidthInPoints = desiredWidthInCm * 72 / 2.54f;
            float desiredHeightInPoints = desiredHeightInCm * 72 / 2.54f;

            // Set the desired width and height of the image in points
            float desiredWidth = desiredWidthInPoints;
            float desiredHeight = desiredHeightInPoints;
            image.scaleToFit(desiredWidth, desiredHeight);

            // Calculate the coordinates to position the image at the bottom
            float x = (pageWidth - desiredWidth) / 2; // Centered horizontally
            float y = document.bottomMargin() - image.getScaledHeight(); // Position from the bottom

            image.setAbsolutePosition(x, y);
            document.add(image);
            // Close the document
            document.close();

            byte[] pdfContent = baos.toByteArray();
            // Set HTTP headers for response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("Inv" + filename + ".pdf")
                    .build());

            // Return the PDF content as ResponseEntity
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        }
        return null;
    }

    private void setCellContentAndFont(PdfPCell cell, String content, Font font) {
        if (content == null) {
            content = ""; // or handle it in some other appropriate way
        }
        // Set initial font size
        float fontSize = 12f;
        float minFontSize = 8f; // Minimum font size for readability

        // Set content and font for the cell
        cell.setPhrase(new Phrase(content, new Font(font.getBaseFont(), fontSize)));

        // Calculate width of content in the cell
        float contentWidth = font.getBaseFont().getWidthPoint(content, fontSize);

        // Calculate available width in the cell
        float availableWidth = cell.getWidth() - cell.getPaddingLeft() - cell.getPaddingRight();

        // If content width is greater than available width, shrink font size
        while (contentWidth > availableWidth && fontSize > minFontSize) {
            fontSize -= 0.1f; // Adjust the decrement value as per need
            cell.setPhrase(new Phrase(content, new Font(font.getBaseFont(), fontSize, font.getStyle(), font.getColor())));
            contentWidth = font.getBaseFont().getWidthPoint(content, fontSize);
        }
    }
}
