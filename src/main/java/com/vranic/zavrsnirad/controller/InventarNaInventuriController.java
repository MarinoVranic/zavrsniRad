package com.vranic.zavrsnirad.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.*;
import com.vranic.zavrsnirad.service.*;
import com.vranic.zavrsnirad.util.CsvGeneratorUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/provodenjeInventure")
@SessionAttributes("trenutnaLokacija")
public class InventarNaInventuriController {

    @Autowired
    private InventarNaInventuriService inventarNaInventuriService;

    @Autowired
    private InventuraService inventuraService;

    @Autowired
    private InventarService inventarService;

    @Autowired
    private CsvGeneratorUtil csvGeneratorUtil;

    @Autowired
    private LokacijaService lokacijaService;

    public LocalDate today = LocalDate.now();




    @GetMapping("/all")
    public String getAllInventarNaInventuri(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInvNaInventuri", inventarNaInventuriService.getAllInventarNaInventuri());
        List<Inventura> allInventura = inventuraService.getAllInventura();
        model.addAttribute("allInventura", allInventura);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        Lokacija trenutnaLokacija = (Lokacija) session.getAttribute("trenutnaLokacija");
        if(trenutnaLokacija != null){
            model.addAttribute("trenutnaLokacija", trenutnaLokacija);
        } else {
            model.addAttribute("errorLokacija", "Prvo morate postaviti lokaciju");
        }

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            return "user/provodenjeInventure";
        }
    }

    @GetMapping("/allActiveState")
    public String showAllActiveState(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInvNaInventuri", inventarNaInventuriService.showAllActiveState());
        List<Inventura> allInventura = inventuraService.getAllInventura();
        model.addAttribute("allInventura", allInventura);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        Lokacija trenutnaLokacija = (Lokacija) session.getAttribute("trenutnaLokacija");
        if(trenutnaLokacija != null){
            model.addAttribute("trenutnaLokacija", trenutnaLokacija);
        } else {
            model.addAttribute("errorLokacija", "Prvo morate postaviti lokaciju");
        }

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            return "user/provodenjeInventure";
        }
    }

    @GetMapping("/allInactiveState")
    public String showAllInactiveState(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInvNaInventuri", inventarNaInventuriService.showAllInactiveState());
        List<Inventura> allInventura = inventuraService.getAllInventura();
        model.addAttribute("allInventura", allInventura);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        Lokacija trenutnaLokacija = (Lokacija) session.getAttribute("trenutnaLokacija");
        if(trenutnaLokacija != null){
            model.addAttribute("trenutnaLokacija", trenutnaLokacija);
        } else {
            model.addAttribute("errorLokacija", "Prvo morate postaviti lokaciju");
        }

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            return "user/provodenjeInventure";
        }
    }

    @GetMapping("/allWriteOff")
    public String showAllWriteOff(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInvNaInventuri", inventarNaInventuriService.showAllWriteOff());
        List<Inventura> allInventura = inventuraService.getAllInventura();
        model.addAttribute("allInventura", allInventura);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        Lokacija trenutnaLokacija = (Lokacija) session.getAttribute("trenutnaLokacija");
        if(trenutnaLokacija != null){
            model.addAttribute("trenutnaLokacija", trenutnaLokacija);
        } else {
            model.addAttribute("errorLokacija", "Prvo morate postaviti lokaciju");
        }

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            return "user/provodenjeInventure";
        }
    }

    @GetMapping("/allNonWriteOff")
    public String showAllNonWriteOff(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInvNaInventuri", inventarNaInventuriService.showAllNonWriteOff());
        List<Inventura> allInventura = inventuraService.getAllInventura();
        model.addAttribute("allInventura", allInventura);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        Lokacija trenutnaLokacija = (Lokacija) session.getAttribute("trenutnaLokacija");
        if(trenutnaLokacija != null){
            model.addAttribute("trenutnaLokacija", trenutnaLokacija);
        } else {
            model.addAttribute("errorLokacija", "Prvo morate postaviti lokaciju");
        }

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            return "user/provodenjeInventure";
        }
    }

    @PostMapping("/odaberi_lokaciju")
    public String odaberiLokaciju(@RequestParam Long idLokacije, Model model, HttpSession session){
        Lokacija trenutnaLokacija = lokacijaService.getLokacijaById(idLokacije);
        model.addAttribute("trenutnaLokacija", trenutnaLokacija);
        session.setAttribute("trenutnaLokacija", trenutnaLokacija);
        return "redirect:/provodenjeInventure/all";
    }

    @PostMapping("/addNew")
    public String addNewScan(@RequestParam("invBroj") String inventarniBroj, Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Lokacija trenutnaLokacija = (Lokacija) session.getAttribute("trenutnaLokacija");
        Integer currentYear = LocalDate.now().getYear();
        Inventar inventar = inventarService.getInventarById(inventarniBroj);
        Inventura inventura = inventuraService.getInventuraById(currentYear.longValue());
        InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
        inventarNaInventuri.setInventar(inventar);
        if (inventar == null) {
            model.addAttribute("error2", "Skenirani inventar nije zaveden u bazi!");
            return getViewBasedOnRole(auth);
        } else if (inventarNaInventuriService.checkIfInventarAlreadyScanned(inventarNaInventuri.getInventar().getInventarniBroj(), currentYear.longValue()) != 0) {
            model.addAttribute("error2", "Inventar je već skeniran!");
            return getViewBasedOnRole(auth);
        } else if (trenutnaLokacija == null) {
            model.addAttribute("error2", "Prvo morate postaviti lokaciju!");
            return getViewBasedOnRole(auth);
        } else {
            if (inventura == null) {
                inventura = new Inventura();
                inventura.setIdInventure(currentYear.longValue());
                inventarNaInventuri.setInventura(inventura);
                inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                inventarNaInventuri.setLokacija(trenutnaLokacija);
                inventarNaInventuri.setStanje("Aktivno");
                inventarNaInventuri.setOtpis("Ne");
//                System.out.println(inventar);
//                System.out.println(currentYear.longValue());
//                System.out.println(inventura.getIdInventure());
                inventarNaInventuriService.save(inventarNaInventuri);
            } else {
                inventarNaInventuri.setInventura(inventura);
                inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                inventarNaInventuri.setLokacija(trenutnaLokacija);
                inventarNaInventuri.setStanje("Aktivno");
                inventarNaInventuri.setOtpis("Ne");
                inventarNaInventuriService.save(inventarNaInventuri);
            }
        }
        return "redirect:/provodenjeInventure/all";
    }

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            return "user/provodenjeInventure";
        }
    }

    @PostMapping("/scanNew")
    public String newScan(@RequestParam("inventarniBr") String inventarniBroj, Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Lokacija trenutnaLokacija = (Lokacija) session.getAttribute("trenutnaLokacija");
        Integer currentYear = LocalDate.now().getYear();
        String vrstaInventara = inventarniBroj.substring(0,1);
        System.out.println(vrstaInventara);
        String invBroj = inventarniBroj.substring(8, 12);
        int invBrSaNulama = Integer.parseInt(invBroj);
        String invBrBezNula = String.valueOf(invBrSaNulama);
//        Inventar inventar = inventarService.getInventarById(invBrBezNula);
//        Inventura inventura = inventuraService.getInventuraById(currentYear.longValue());
//        InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
//        inventarNaInventuri.setInventar(inventar);
//        inventarNaInventuri.setLokacija(trenutnaLokacija);
        if(vrstaInventara.equals("2"))
        {
            invBrBezNula = "SI" + invBrBezNula;
            Inventar inventar = inventarService.getInventarById(invBrBezNula);
            Inventura inventura = inventuraService.getInventuraById(currentYear.longValue());
            InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
            inventarNaInventuri.setInventar(inventar);
            inventarNaInventuri.setLokacija(trenutnaLokacija);
            if (inventar == null) {
                model.addAttribute("error3", "Skenirani inventar nije zaveden u bazi!");
                return getViewBasedOnRole(auth);
            } else if (inventarNaInventuriService.checkIfInventarAlreadyScanned(inventarNaInventuri.getInventar().getInventarniBroj(), currentYear.longValue()) != 0) {
                model.addAttribute("error3", "Inventar je već skeniran!");
                return getViewBasedOnRole(auth);
            } else if (trenutnaLokacija == null) {
                model.addAttribute("error3", "Prvo morate postaviti lokaciju!");
                return getViewBasedOnRole(auth);
            } else {
                if (inventura == null) {
                    inventura = new Inventura();
                    inventura.setIdInventure(currentYear.longValue());
                    inventarNaInventuri.setInventura(inventura);
                    inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                    inventarNaInventuri.setLokacija(trenutnaLokacija);
                    inventarNaInventuri.setStanje("Aktivno");
                    inventarNaInventuri.setOtpis("Ne");
//                    System.out.println(inventar);
//                    System.out.println(currentYear.longValue());
//                    System.out.println(inventura.getIdInventure());
                    inventarNaInventuriService.save(inventarNaInventuri);
                } else {
                    inventarNaInventuri.setInventura(inventura);
                    inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                    inventarNaInventuri.setLokacija(trenutnaLokacija);
                    inventarNaInventuri.setStanje("Aktivno");
                    inventarNaInventuri.setOtpis("Ne");
                    inventarNaInventuriService.save(inventarNaInventuri);
                }
            }
        } else {
            Inventar inventar = inventarService.getInventarById(invBrBezNula);
            Inventura inventura = inventuraService.getInventuraById(currentYear.longValue());
            InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
            inventarNaInventuri.setInventar(inventar);
            inventarNaInventuri.setLokacija(trenutnaLokacija);
            if (inventar == null) {
                model.addAttribute("error3", "Skenirani inventar nije zaveden u bazi!");
                return getViewBasedOnRole(auth);
            } else if (inventarNaInventuriService.checkIfInventarAlreadyScanned(inventarNaInventuri.getInventar().getInventarniBroj(), currentYear.longValue()) != 0) {
                model.addAttribute("error3", "Inventar je već skeniran!");
                return getViewBasedOnRole(auth);
            } else if (trenutnaLokacija == null) {
                model.addAttribute("error3", "Prvo morate postaviti lokaciju!");
                return getViewBasedOnRole(auth);
            } else {
                if (inventura == null) {
                    inventura = new Inventura();
                    inventura.setIdInventure(currentYear.longValue());
                    inventarNaInventuri.setInventura(inventura);
                    inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                    inventarNaInventuri.setLokacija(trenutnaLokacija);
                    inventarNaInventuri.setStanje("Aktivno");
                    inventarNaInventuri.setOtpis("Ne");
//                    System.out.println(inventar);
//                    System.out.println(currentYear.longValue());
//                    System.out.println(inventura.getIdInventure());
                    inventarNaInventuriService.save(inventarNaInventuri);
                } else {
                    inventarNaInventuri.setInventura(inventura);
                    inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
                    inventarNaInventuri.setLokacija(trenutnaLokacija);
                    inventarNaInventuri.setStanje("Aktivno");
                    inventarNaInventuri.setOtpis("Ne");
                    inventarNaInventuriService.save(inventarNaInventuri);
                }
            }
        }
//        if (inventar == null) {
//            model.addAttribute("error3", "Skenirani inventar nije zaveden u bazi!");
//            return getViewBasedOnRole(auth);
//        } else if (inventarNaInventuriService.checkIfInventarAlreadyScanned(inventarNaInventuri.getInventar().getInventarniBroj(), currentYear.longValue()) != 0) {
//            model.addAttribute("error3", "Inventar je već skeniran!");
//            return getViewBasedOnRole(auth);
//        } else if (trenutnaLokacija == null) {
//            model.addAttribute("error3", "Prvo morate postaviti lokaciju!");
//            return getViewBasedOnRole(auth);
//        } else {
//            if (inventura == null) {
//                inventura = new Inventura();
//                inventura.setIdInventure(currentYear.longValue());
//                inventarNaInventuri.setInventura(inventura);
//                inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
//                inventarNaInventuri.setLokacija(trenutnaLokacija);
//                System.out.println(inventar);
//                System.out.println(currentYear.longValue());
//                System.out.println(inventura.getIdInventure());
//                inventarNaInventuriService.save(inventarNaInventuri);
//            } else {
//                inventarNaInventuri.setInventura(inventura);
//                inventarNaInventuri.setDatumSkeniranja(LocalDateTime.now());
//                inventarNaInventuri.setLokacija(trenutnaLokacija);
//                inventarNaInventuriService.save(inventarNaInventuri);
//            }
//        }
        return "redirect:/provodenjeInventure/all";
    }

    @GetMapping("delete/{idSkeniranja}")
    public String deleteById(@PathVariable(value = "idSkeniranja") Long idSkeniranja) {
        inventarNaInventuriService.deleteById(idSkeniranja);
        return "redirect:/provodenjeInventure/all";
    }

    @GetMapping("/changeLocation/{inventarniBroj}/{idInventure}")
    @Transactional
    public String changeLocation(@PathVariable(value = "inventarniBroj") String inventarniBroj, @PathVariable(value = "idInventure") Long idInventure){
        InventarNaInventuri inventarNaInventuri = inventarNaInventuriService.selectInvNaInvByBrojAndInventura(inventarniBroj, idInventure);
        inventarNaInventuriService.changeLokacija(inventarNaInventuri.getLokacija().getIdLokacije(), inventarniBroj);
        return "redirect:/provodenjeInventure/all";
    }

    @GetMapping("/changeState/{idSkeniranja}")
    @Transactional
    public String changeState(@PathVariable(value = "idSkeniranja") Long idskeniranja){
        InventarNaInventuri inventarNaInventuri = inventarNaInventuriService.findById(idskeniranja);
        if(inventarNaInventuri.getStanje().equals("Aktivno"))
        {
            inventarNaInventuriService.changeStanje("Neaktivno", idskeniranja);
        } else {
            inventarNaInventuriService.changeStanje("Aktivno", idskeniranja);
        }
        return "redirect:/provodenjeInventure/all";
    }

    @GetMapping("/changeWriteOff/{idSkeniranja}")
    @Transactional
    public String changeWriteOff(@PathVariable(value = "idSkeniranja") Long idskeniranja){
        InventarNaInventuri inventarNaInventuri = inventarNaInventuriService.findById(idskeniranja);
        if(inventarNaInventuri.getOtpis().equals("Ne"))
        {
            inventarNaInventuriService.changeOtpis("Da", idskeniranja);
        } else {
            inventarNaInventuriService.changeOtpis("Ne", idskeniranja);
        }
        return "redirect:/provodenjeInventure/all";
    }

    @RequestMapping("/find")
    public String findByInventarniBroj(@RequestParam("inventarniBroj") String inventarniBroj, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.findByInventarniBroj(inventarniBroj);

        if (inventarNaInventuriList.isEmpty()) {
            model.addAttribute("error", "Inventar pod tim brojem nije skeniran!");
        } else {
            model.addAttribute("savInvNaInventuri", inventarNaInventuriList);
        }

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
            model.addAttribute("invNaInv", inventarNaInventuri);
            return "user/provodenjeInventure";
        }
    }

    @GetMapping("/findByGodinaInventure")
    public String showInventarByGodinaInventure(@RequestParam("idInventure") Long idInventure, @RequestParam("tipInventara") String tipInventara, @RequestParam("isFound") String isFound, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventura> allInventura = inventuraService.getAllInventura();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();

        if (tipInventara.equals("SI")) {
            if (isFound.equals("Pronađeno")) {
                List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.SIByGodinaInventure(idInventure);
                model.addAttribute("allInventura", allInventura);
                model.addAttribute("allLokacija", allLokacija);
                model.addAttribute("savInvNaInventuri", inventarNaInventuriList);
            } else if (isFound.equals("Nepronađeno")){
                List<Inventar> inventarList = inventarNaInventuriService.reportSIByInventuraAndNotFound(idInventure);
                model.addAttribute("allInventura", allInventura);
                model.addAttribute("allLokacija", allLokacija);
                model.addAttribute("savInventar", inventarList);
            }
        } else if (tipInventara.equals("OS")) {
            if (isFound.equals("Pronađeno")) {
                List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.OSByGodinaInventure(idInventure);
                model.addAttribute("allInventura", allInventura);
                model.addAttribute("allLokacija", allLokacija);
                model.addAttribute("savInvNaInventuri", inventarNaInventuriList);
            } else if (isFound.equals("Nepronađeno")) {
                List<Inventar> inventarList = inventarNaInventuriService.reportOSByInventuraAndNotFound(idInventure);
//                System.out.println(inventarList);
                model.addAttribute("allInventura", allInventura);
                model.addAttribute("allLokacija", allLokacija);
                model.addAttribute("savInventar", inventarList);
            }
        }
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "inventura/provodenjeInventure";
        } else {
            // User has user role
            InventarNaInventuri inventarNaInventuri = new InventarNaInventuri();
            model.addAttribute("invNaInv", inventarNaInventuri);
            return "user/provodenjeInventure";
        }
    }

    @GetMapping("/generatePDF")
    public void generatePDF(@RequestParam("idInventure") Long idInventure, @RequestParam("tipInventara") String tipInventara, @RequestParam("isFound") String isFound, HttpServletResponse response) throws IOException, DocumentException {
        if(isFound.equals("Pronađeno")) {
            // Set the content type and attachment header
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"Izvjestaj_inventure_za-" + tipInventara + "_" + idInventure + ".pdf\"");

            // Create a new PDF document
            Document document = new Document(PageSize.A4.rotate());

            // Create a PdfWriter instance to write the document to the response output stream
            PdfWriter.getInstance(document, response.getOutputStream());

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
            String imagePath2 = "static/images/Aitac Logo Blue Background HiRes.jpg"; // Relative path to the image file
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
            Phrase headerPhrase = new Phrase("PRONAĐENI INVENTAR NA INVENTURI " + idInventure, boldFont);
            header.add(headerPhrase);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20); // Adjust the value as per your requirement
            document.add(header);

            //Adding date of the report
            Paragraph printDate = new Paragraph();
            String todayDate = "Datum izvještaja: " + today;
            Font dateFont = new Font(arialBoldItalicFont, 10, Font.ITALIC);
            Phrase datePhrase = new Phrase(todayDate, dateFont);
            printDate.add(datePhrase);
            printDate.setAlignment(Element.ALIGN_LEFT);
            printDate.setSpacingAfter(5);
            document.add(printDate);

            // Create a table with 15 columns
            PdfPTable table = new PdfPTable(7);

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
            headerCell.setPhrase(new Phrase("Inventura", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Skenirani inventar", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Naziv inventara", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Vrsta inventara", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Datum skeniranja", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Upisana lokacija inventara", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Skenirana lokacija inventara", headerFont));
            table.addCell(headerCell);

            // Get the list of Inventar objects from service
            if (tipInventara.equals("SI")) {
                List<InventarNaInventuri> scannedInventar = inventarNaInventuriService.SIByGodinaInventure(idInventure);
                for (InventarNaInventuri inventar : scannedInventar) {
                    dataCell.setPhrase(new Phrase(inventar.getInventura().getIdInventure().toString(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getInventarniBroj(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getNazivUredaja(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getVrstaUredaja().getNazivVrsteUredaja(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getDatumSkeniranja().toString(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getLokacija().getNazivLokacije(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getLokacija().getNazivLokacije(), croatianFont));
                    table.addCell(dataCell);
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
            } else {
                List<InventarNaInventuri> scannedInventar = inventarNaInventuriService.OSByGodinaInventure(idInventure);

                // Add data cells to the table
                for (InventarNaInventuri inventar : scannedInventar) {
                    dataCell.setPhrase(new Phrase(inventar.getInventura().getIdInventure().toString(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getInventarniBroj(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getNazivUredaja(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getVrstaUredaja().getNazivVrsteUredaja(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getDatumSkeniranja().toString(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getInventar().getLokacija().getNazivLokacije(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getLokacija().getNazivLokacije(), croatianFont));
                    table.addCell(dataCell);
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
            }
        } else if(isFound.equals("Nepronađeno")){
            // Set the content type and attachment header
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"Izvjestaj_nepronadenog-" + tipInventara + "_" + idInventure + ".pdf\"");

            // Create a new PDF document
            Document document = new Document(PageSize.A4.rotate());

            // Create a PdfWriter instance to write the document to the response output stream
            PdfWriter.getInstance(document, response.getOutputStream());

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
            String imagePath2 = "static/images/Aitac Logo Blue Background HiRes.jpg"; // Relative path to the image file
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
            Phrase headerPhrase = new Phrase("NEPRONAĐENI INVENTAR NA INVENTURI " + idInventure, boldFont);
            header.add(headerPhrase);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20); // Adjust the value as per your requirement
            document.add(header);

            //Adding date of the report
            Paragraph printDate = new Paragraph();
            String todayDate = "Datum izvještaja: " + today;
            Font dateFont = new Font(arialBoldItalicFont, 10, Font.ITALIC);
            Phrase datePhrase = new Phrase(todayDate, dateFont);
            printDate.add(datePhrase);
            printDate.setAlignment(Element.ALIGN_LEFT);
            printDate.setSpacingAfter(5);
            document.add(printDate);

            // Create a table with 15 columns
            PdfPTable table = new PdfPTable(6);

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
            headerCell.setPhrase(new Phrase("Inventarni broj", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Naziv osnovnog sredstva", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Vrsta osnovnog sredstva", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Zaduženo na", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Upisana lokacija osnovnog sredstva", headerFont));
            table.addCell(headerCell);
            headerCell.setPhrase(new Phrase("Nabavna vrijednost", headerFont));
            table.addCell(headerCell);

            // Get the list of Inventar objects from service
            if (tipInventara.equals("SI")) {
                List<Inventar> notFoundInventar = inventarNaInventuriService.reportSIByInventuraAndNotFound(idInventure);
                for (Inventar inventar : notFoundInventar) {
                    dataCell.setPhrase(new Phrase(inventar.getInventarniBroj(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getNazivUredaja(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont));
                    table.addCell(dataCell);
                    if(inventar.getKorisnik() != null){
                        dataCell.setPhrase(new Phrase(inventar.getKorisnik().getFirstName() + " " + inventar.getKorisnik().getLastName(), croatianFont));
                        table.addCell(dataCell);
                    } else {
                        dataCell.setPhrase(new Phrase("", croatianFont));
                        table.addCell(dataCell);
                    }
                    dataCell.setPhrase(new Phrase(inventar.getLokacija().getNazivLokacije(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getNabavnaVrijednost().toString() + " EUR", croatianFont));
                    table.addCell(dataCell);
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
            } else {
                List<Inventar> notFoundInventar = inventarNaInventuriService.reportOSByInventuraAndNotFound(idInventure);

                // Add data cells to the table
                for (Inventar inventar : notFoundInventar) {
                    dataCell.setPhrase(new Phrase(inventar.getInventarniBroj(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getNazivUredaja(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont));
                    table.addCell(dataCell);
                    if(inventar.getKorisnik() != null){
                        dataCell.setPhrase(new Phrase(inventar.getKorisnik().getFirstName() + " " + inventar.getKorisnik().getLastName(), croatianFont));
                        table.addCell(dataCell);
                    } else {
                        dataCell.setPhrase(new Phrase("", croatianFont));
                        table.addCell(dataCell);
                    }
                    dataCell.setPhrase(new Phrase(inventar.getLokacija().getNazivLokacije(), croatianFont));
                    table.addCell(dataCell);
                    dataCell.setPhrase(new Phrase(inventar.getNabavnaVrijednost().toString() + " EUR", croatianFont));
                    table.addCell(dataCell);
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
            }
        }
    }

    @GetMapping("/generateCSV")
    public ResponseEntity<byte[]> generateCsvFile(@RequestParam("idInventure") Long idInventure, @RequestParam("tipInventara") String tipInventara, @RequestParam("isFound") String isFound) {
        String replacedIsFound = isFound.replace("đ", "d");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Inventura-" + idInventure + "_za_" + tipInventara + "_" + replacedIsFound + ".csv");

        byte[] csvBytes = csvGeneratorUtil.generateCsv(idInventure, isFound, tipInventara).getBytes();

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
}
