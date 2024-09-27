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
import java.util.List;

@Controller
@RequestMapping("/inventar")
public class InventarController {
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

    public LocalDate getToday() {
        return LocalDate.now();
    }

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "admin/inventar";
        } else {
            // User has user role
            return "inventar/inventar";
        }
    }

    @GetMapping("/all")
    public String getAllInventar(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInventar", inventarService.getInventarForAdministration());
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        model.addAttribute("allKorisnik", allKorisnik);
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
        return "inventar/updateInventar";
    }

    @GetMapping("/addNew")
    public String addNewInventar(Model model) throws Exception {
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
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        model.addAttribute("allKorisnik", allKorisnik);
        return "inventar/newInventar";
    }

    @PostMapping("/addNew")
    public String addInventar(@ModelAttribute("inventar") Inventar inventar, Model model) throws Exception {
        if (inventarService.checkIfInvBrojIsAvailable(inventar.getInventarniBroj()) != 0) {
            model.addAttribute("error", "Inventarni broj već postoji!");
            return "inventar/newInventar";
        } else {
            // Set the username to null if it is blank
            if (StringUtils.isBlank(inventar.getKorisnik().getUsername())) {
                inventar.setKorisnik(null);
            }
            inventarService.save(inventar);
        }
        return "redirect:/inventar/all";
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
        return "redirect:/inventar/all";
    }

    @GetMapping("delete/{inventarniBroj}")
    public String deleteById(@PathVariable(value = "inventarniBroj") String inventarniBroj) {
        inventarService.deleteById(inventarniBroj);
        return "redirect:/inventar/all";
    }

    @GetMapping("/find")
    public String findInventarByName(@RequestParam("inventarniBroj") String inventarniBroj, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventar> inventarList = inventarService.findInventarByInvBroj(inventarniBroj);
        if (inventarList.isEmpty()) {
            model.addAttribute("error", "Inventar pod tim brojem ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getInventarForAdministration());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
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
        if (inventarList.isEmpty()) {
            model.addAttribute("error2", "Inventar tog serijskog broja ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getAllInventar());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
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
        return "inventar/zaduziInventar";
    }

    @PostMapping("/saveZaduzenje")
    @Transactional
    public String zaduzenjeInventara(@ModelAttribute("inventar") Inventar inventar) throws Exception {
        Integer idVrste = inventar.getVrstaUredaja().getIdVrsteUredaja().intValue();
        if(idVrste.equals(1)||idVrste.equals(6)){
            //split string nazivUredaja by one or more spaces and -
            String [] nazivUredaja = inventar.getNazivUredaja().split("\\s+|-");
            String firstPartOfHostname = nazivUredaja[0];
            String secondPartOfHostname = inventar.getKorisnik().getUsername();
            String hostname = firstPartOfHostname+"-"+secondPartOfHostname;
            inventarService.zaduziInventar(hostname, inventar.getLokacija().getIdLokacije(), inventar.getKorisnik().getUsername(),
                    getToday(), inventar.getInventarniBroj());
        }else {
            inventarService.zaduziInventar(inventar.getHostname(), inventar.getLokacija().getIdLokacije(), inventar.getKorisnik().getUsername(),
                    getToday(), inventar.getInventarniBroj());
        }
        return "redirect:/inventar/all";
    }

    @GetMapping("razduzi/{inventarniBroj}")
    @Transactional
    public String razduziInventar(@PathVariable(value = "inventarniBroj") String inventarniBroj) throws Exception {
        inventarService.razduziInventar(getToday(), inventarniBroj);
        return "redirect:/inventar/all";
    }

    @GetMapping("/findByVrsta")
    public String showInventarByVrstaUredaja(@RequestParam("idVrsteUredaja") Long idVrsteUredaja, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        List<Inventar> inventarList = inventarService.getInventarByVrstaUredaja(idVrsteUredaja);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        model.addAttribute("allKorisnik", allKorisnik);
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

    @GetMapping("/findByTipInventara")
    public String showInventarByTipInventara(@RequestParam("tipInventara") String tipInventara, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(tipInventara.equals("OS"))
        {
            List<Inventar> inventarList = inventarService.getInventarByOS();
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
            List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
            List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
            List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
            model.addAttribute("allKorisnik", allKorisnik);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("allLokacija", allLokacija);
            return getViewBasedOnRole(auth);
        } else if(tipInventara.equals("IT")) {
            List<Inventar> inventarList = inventarService.getInventarByIT();
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
            List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
            List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
            List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
            model.addAttribute("allKorisnik", allKorisnik);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("allLokacija", allLokacija);
            return getViewBasedOnRole(auth);
        } else {
            List<Inventar> inventarList = inventarService.getInventarBySI();
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
            List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
            List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
            List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
            model.addAttribute("allKorisnik", allKorisnik);
            model.addAttribute("allVrstaUredaja", allVrstaUredaja);
            model.addAttribute("allLokacija", allLokacija);
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
                } else {
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "1" + inventarniBroj.substring(1);
                }
            }
            else {
                String sitniInventar = originalInventarniBroj.substring(0,2);
                System.out.println(sitniInventar);
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
                } else {
                    while(inventarniBroj.length() < 12){
                        inventarniBroj = "0" + inventarniBroj;
                    }
                    inventarniBroj = "1" + inventarniBroj.substring(1);
                }
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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ OSNOVNIH SREDSTAVA", boldFont);
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
        PdfPTable table = new PdfPTable(10);

        // Set the table width as a percentage of the available page width
        table.setWidthPercentage(100);

        // Set table header cell styles
        Font headerFont = new Font(arialBoldFont, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(5);

        setCellContentAndFont(headerCell,"Inventarni broj", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Naziv uređaja", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Serijski broj", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Vrsta uređaja", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Lokacija", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Zaduženo na korisnika", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Račun/Dokument", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Dobavljač", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Nabavna vrijednost", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Napomena", headerFont);
        table.addCell(headerCell);

        // Get the list of Inventar objects from service
        List<Inventar> inventari = inventarService.getInventarForAdministration();

        // Add data cells to the table
        for (Inventar inventar : inventari) {

            PdfPCell cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            setCellContentAndFont(cell, inventar.getInventarniBroj(), croatianFont);
            table.addCell(cell);
            setCellContentAndFont(cell, inventar.getNazivUredaja(), croatianFont);
            table.addCell(cell);
            if(inventar.getSerijskiBroj() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getSerijskiBroj(), croatianFont);
            }
            table.addCell(cell);
            setCellContentAndFont(cell, inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont);
            table.addCell(cell);
            if(inventar.getLokacija() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getLokacija().getNazivLokacije(), croatianFont);
            }
            table.addCell(cell);
            Korisnik korisnik = inventar.getKorisnik();
            if(korisnik != null && korisnik.getUsername() != null){
                setCellContentAndFont(cell, korisnik.getFirstName() + " " + korisnik.getLastName(), croatianFont);
            } else {
                setCellContentAndFont(cell, "", croatianFont);
            }
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
            setCellContentAndFont(cell, inventar.getNabavnaVrijednost() + " EUR", croatianFont);
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
                .filename("Izvjestaj_inventar.pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    // Method to set content and font for cell with font shrinking if necessary
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
