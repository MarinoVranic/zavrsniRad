package com.vranic.zavrsnirad.controller;

import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.*;
import com.vranic.zavrsnirad.service.*;
import com.vranic.zavrsnirad.util.BarcodeImageUtils;
import io.micrometer.common.util.StringUtils;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/phones")
public class MobitelController {
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
    private MobilnaTarifaService mobilnaTarifaService;

    public LocalDate getToday() {
        return LocalDate.now();
    }
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "admin/mobitel";
        } else {
            // User has user role
            return "inventar/mobitel/mobitel";
        }
    }

    @GetMapping("/all")
    public String getAllMobitel(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("savInventar", inventarService.getAllMobitel());
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        model.addAttribute("allLokacija", allLokacija);
        List<MobilnaTarifa> allTarifa = mobilnaTarifaService.getAllMobilnaTarifa();
        model.addAttribute("allTarifa", allTarifa);
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/{inventarniBroj}")
    public Inventar getInventarById(@PathVariable String inventarniBroj) {
        return inventarService.getInventarById(inventarniBroj);
    }

    @GetMapping("/update/{inventarniBroj}")
    public String updateInventar(@PathVariable(value = "inventarniBroj") String inventarniBroj, Model model) throws Exception {
        Inventar inventar = inventarService.getInventarById(inventarniBroj);
        Lokacija selectedLokacija = inventar.getLokacija();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        Korisnik selectedKorisnik = inventar.getKorisnik();
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        Dobavljac selectedDobavljac = inventar.getDobavljac();
        List<Dobavljac> allDobavljac = dobavljacService.getAllDobavljaci();
        Racun selectedRacun = inventar.getRacun();
        List<Racun> allRacun = racunService.getAllRacunForDropdown();
        MobilnaTarifa selectedMobilnaTarifa = inventar.getMobilnaTarifa();
        List<MobilnaTarifa> allMobilnaTarifa = mobilnaTarifaService.getAllMobilnaTarifa();

        model.addAttribute("inventar", inventar);
        model.addAttribute("selectedLokacija", selectedLokacija);
        model.addAttribute("allLokacija", allLokacija);
        model.addAttribute("selectedKorisnik", selectedKorisnik);
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("selectedDobavljac", selectedDobavljac);
        model.addAttribute("allDobavljac", allDobavljac);
        model.addAttribute("selectedRacun", selectedRacun);
        model.addAttribute("allRacun", allRacun);
        model.addAttribute("selectedMobilnaTarifa", selectedMobilnaTarifa);
        model.addAttribute("allMobilnaTarifa", allMobilnaTarifa);
        return "inventar/mobitel/updateMobitel";
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
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        model.addAttribute("allKorisnik", allKorisnik);
        List<MobilnaTarifa> allTarifa = mobilnaTarifaService.getAllMobilnaTarifa();
        model.addAttribute("allTarifa", allTarifa);
        return "inventar/mobitel/newMobitel";
    }

    @PostMapping("/addNew")
    public String addInventar(@ModelAttribute("inventar") Inventar inventar, Model model) throws Exception {
        if (inventarService.checkIfInvBrojIsAvailable(inventar.getInventarniBroj()) != 0) {
            model.addAttribute("error", "Inventarni broj već postoji!");
            return "inventar/mobitel/newMobitel";
        } else {
            // Set the username to null if it is blank
            if (StringUtils.isBlank(inventar.getKorisnik().getUsername())) {
                inventar.setKorisnik(null);
            }
            //set Racun to null if it is already null
            if (inventar.getRacun() == null || inventar.getRacun().getIdRacuna() == null) {
                inventar.setRacun(null);
            }
            inventar.setVrstaUredaja(vrstaUredajaService.getVrstaUredajaById(20L));
            inventarService.save(inventar);
        }
        return "redirect:/phones/all";
    }

    @PostMapping("/save")
    public String saveMobitel(@ModelAttribute("inventar") Inventar inventar) throws Exception {
        // Set the username to null if it is blank
        if (StringUtils.isBlank(inventar.getKorisnik().getUsername())) {
            inventar.setKorisnik(null);
        }
        //set Racun to null if it is already null
        if (inventar.getRacun() == null || inventar.getRacun().getIdRacuna() == null) {
            inventar.setRacun(null);
        }
        inventar.setVrstaUredaja(vrstaUredajaService.getVrstaUredajaById(20L));
        inventarService.save(inventar);
        return "redirect:/phones/all";
    }

    @GetMapping("delete/{inventarniBroj}")
    public String deleteById(@PathVariable(value = "inventarniBroj") String inventarniBroj) {
        inventarService.deleteById(inventarniBroj);
        return "redirect:/phones/all";
    }

    @GetMapping("/find")
    public String findInventarByName(@RequestParam("inventarniBroj") String inventarniBroj, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventar> inventarList = inventarService.findInventarByInvBroj(inventarniBroj);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<MobilnaTarifa> allTarifa = mobilnaTarifaService.getAllMobilnaTarifa();
        if (inventarList.isEmpty()) {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("error", "Mobitel pod tim brojem ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getAllMobitel());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findBySerialNumber")
    public String findMobitelBySerialNumber(@RequestParam("serialNumber") String serijskiBroj, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Inventar> inventarList = inventarService.getMobitelBySerijskiBroj(serijskiBroj);
        List<MobilnaTarifa> allTarifa = mobilnaTarifaService.getAllMobilnaTarifa();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        if (inventarList.isEmpty()) {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allTarifa", allTarifa);
            model.addAttribute("error2", "Mobitel tog serijskog broja ne postoji u sustavu!");
            model.addAttribute("savInventar", inventarService.getAllMobitel());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allTarifa", allTarifa);
            model.addAttribute("savInventar", inventarList);
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findByUser")
    public String showMobitelByUser(@RequestParam("lastName") String lastName, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<MobilnaTarifa> allTarifa = mobilnaTarifaService.getAllMobilnaTarifa();
        List<Inventar> inventarList = inventarService.getMobitelByUser(lastName);
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        if (inventarList.isEmpty()) {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allTarifa", allTarifa);
            model.addAttribute("error3", "Korisnik traženog prezimena ne zadužuje mobitel!");
            model.addAttribute("savInventar", inventarService.getAllMobitel());
            Inventar inventar = new Inventar();
            model.addAttribute("inventar", inventar);
        } else {
            model.addAttribute("allLokacija", allLokacija);
            model.addAttribute("allTarifa", allTarifa);
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
        return "inventar/mobitel/zaduziMobitel";
    }

    @PostMapping("/saveZaduzenje")
    @Transactional
    public String zaduzenjeInventara(@ModelAttribute("inventar") Inventar inventar) throws Exception {
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
        return "redirect:/phones/all";
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
        return "redirect:/phones/all";
    }

    @GetMapping("razlikaPlacena/{inventarniBroj}")
    @Transactional
    public String razlikaPlacena(@PathVariable(value = "inventarniBroj") String inventarniBroj) throws Exception{
        inventarService.razlikaPlacena(inventarniBroj);
        return "redirect:/phones/all";
    }

    @GetMapping("/findByLokacija")
    public String showInventarByLokacija(@RequestParam("idLokacije") Long idLokacije, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<MobilnaTarifa> allTarifa = mobilnaTarifaService.getAllMobilnaTarifa();
        List<Inventar> inventarList = inventarService.getMobitelByLokacija(idLokacije);
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        model.addAttribute("allTarifa", allTarifa);
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        model.addAttribute("allLokacija", allLokacija);
        model.addAttribute("savInventar", inventarList);
        Inventar inventar = new Inventar();
        model.addAttribute("inventar", inventar);
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findByTarifa")
    public String showInventarByTarifa(@RequestParam("idTarife") Long idTarife, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Lokacija> allLokacija = lokacijaService.getAllLokacija();
        List<MobilnaTarifa> allTarifa = mobilnaTarifaService.getAllMobilnaTarifa();
        List<Inventar> inventarList = inventarService.getMobitelByTarifa(idTarife);
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        List<VrstaUredaja> allVrstaUredaja = vrstaUredajaService.getAllVrstaUredaja();
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("allVrstaUredaja", allVrstaUredaja);
        model.addAttribute("allLokacija", allLokacija);
        model.addAttribute("allTarifa", allTarifa);
        model.addAttribute("savInventar", inventarList);
        Inventar inventar = new Inventar();
        model.addAttribute("inventar", inventar);
        return getViewBasedOnRole(auth);
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
    public ResponseEntity<byte[]> generatePDF() throws Exception {
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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O SLUŽBENIM MOBITELIMA", boldFont);
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
        setCellContentAndFont(headerCell,"Lokacija", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Korisnik", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Broj mobitela", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Mobilna tarifa", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Nabavna vrijednost", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Razlika cijene plaćena", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Datum zaduženja", headerFont);
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
        List<Inventar> inventari = inventarService.getAllMobitel();

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
            if(inventar.getBrojMobitela() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getBrojMobitela(), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getMobilnaTarifa() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getMobilnaTarifa().getNazivTarife(), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getNabavnaVrijednost() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getNabavnaVrijednost().toPlainString() + " EUR", croatianFont);
            }
            table.addCell(cell);
            if (inventar.getNabavnaVrijednost() != null) {
                if (inventar.isRazlikaCijenePlacena() && inventar.getNabavnaVrijednost().intValue() > 650) {
                    setCellContentAndFont(cell, "Razlika cijene plaćena.", croatianFont);
                } else if (!inventar.isRazlikaCijenePlacena() && inventar.getNabavnaVrijednost().intValue() > 650) {
                    setCellContentAndFont(cell, "Razlika cijene nije plaćena.", croatianFont);
                } else {
                    setCellContentAndFont(cell, "", croatianFont);
                }
            } else {
                setCellContentAndFont(cell, "", croatianFont); // or handle the null case appropriately
            }
            table.addCell(cell);
            if(inventar.getDatumZaduzenja() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, String.valueOf(inventar.getDatumZaduzenja()), croatianFont);
            }
            table.addCell(cell);
            if(inventar.getWarrantyEnding() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, String.valueOf(inventar.getWarrantyEnding()), croatianFont);
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
                .filename("Mobiteli-izvjestaj.pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/printPDFpotvrde/{inventarniBroj}")
    public ResponseEntity<byte[]> printPDFpotvrde(@PathVariable(value = "inventarniBroj") String inventarniBroj) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        Inventar inventar = inventarService.getInventarById(inventarniBroj);

        String filename = "PotvrdaPlaceneRazlike-InvBr" + inventarniBroj;

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



        Paragraph aitacPodaci1 = new Paragraph();
        Font fontBlueLine = new Font(arialNormalFont, 5, Font.BOLD);
        fontBlueLine.setColor(35, 47, 131);
        Chunk blueLine = new Chunk("_______________________________________________________________________________________________________________________________ \n", fontBlueLine);
        aitacPodaci1.add(blueLine);
        Font fontAitacPodaci = new Font(arialNormalFont, 6, Font.NORMAL);
        fontAitacPodaci.setColor(160, 160, 160);
        Phrase prviRed = new Phrase("AITAC d.o.o. Istarska cesta 1, 51215 Kastav, Hrvatska · T:+385 51 626 712 · F:+385 51 626 720 · E:info@aitac.nl · W: www.aitac.nl", fontAitacPodaci);
        aitacPodaci1.setAlignment(Element.ALIGN_CENTER);
        aitacPodaci1.add(prviRed);
        aitacPodaci1.setSpacingBefore(30);
        document.add(aitacPodaci1);

        Paragraph aitacPodaci2 = new Paragraph();
        Phrase drugiRed = new Phrase("Temeljni kapital: 18.000,00 kn uplaćen kod ZAP Rijeka, Hrvatska · Članovi uprave: M. Lorencin, MBS:040226601, Trgovački Sud u Rijeci", fontAitacPodaci);
        aitacPodaci2.setAlignment(Element.ALIGN_CENTER);
        aitacPodaci2.add(drugiRed);
        aitacPodaci2.add(blueLine);
        aitacPodaci2.setSpacingAfter(30);
        document.add(aitacPodaci2);

        //Adding date of the report
        Paragraph printDate = new Paragraph();
        String datum = "U Kastvu ";
        String todayDate =  getToday().format(formatter);
        Font datumFont = new Font(arialNormalFont, 12, Font.NORMAL);
        Font dateFont = new Font(arialBoldFont, 12, Font.BOLD);
        Phrase datumPhrase = new Phrase(datum, datumFont);
        Phrase datePhrase = new Phrase(todayDate, dateFont);
        printDate.add(datumPhrase);
        printDate.add(datePhrase);
        printDate.setAlignment(Element.ALIGN_LEFT);
        printDate.setSpacingAfter(20);
        printDate.setIndentationLeft(20);
        document.add(printDate);

        Paragraph header = new Paragraph();
        Font boldFont = new Font(arialBoldItalicFont, 18, Font.BOLD);
        Phrase headerPhrase = new Phrase("POTVRDA O PLAĆANJU RAZLIKE \n ZA NABAVKU MOBITELA", boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(75);
        header.setSpacingBefore(50);
        document.add(header);

        Paragraph tijelo = new Paragraph();
        Phrase textTijela = new Phrase();

        BigDecimal baseValue = new BigDecimal("650");
        BigDecimal nabavnaVrijednost = (inventar.getNabavnaVrijednost() != null) ? inventar.getNabavnaVrijednost() : BigDecimal.ZERO;

        // Calculate razlikeCijene based on the inventory value
        String razlikeCijene = nabavnaVrijednost.subtract(baseValue).toString();

        Font fontNormal = new Font(arialNormalFont, 12, Font.NORMAL);
        Font fontBold = new Font(arialBoldFont, 12, Font.BOLD);
        textTijela.add(new Chunk("Ovom potvrdom potvrđujem da je ", fontNormal));
        if(inventar.getKorisnik()!=null){
            textTijela.add(new Chunk(inventar.getKorisnik().getFirstName() + " " + inventar.getKorisnik().getLastName(), fontBold));
        } else {
            textTijela.add(new Chunk(" ", fontBold));
        }
        textTijela.add(new Chunk(" platio/la niže navedenom, dogovoreni iznos razlike u cijeni od ", fontNormal));
        textTijela.add(new Chunk(razlikeCijene + " EUR", fontBold));
        textTijela.add(new Chunk(" prilikom nabave mobitela ", fontNormal));
        textTijela.add(new Chunk(inventar.getNazivUredaja(), fontBold));
        textTijela.add(new Chunk(" dodijeljenog inventarnog broja ", fontNormal));
        textTijela.add(new Chunk(inventar.getInventarniBroj() + ".", fontBold));
        textTijela.add(new Chunk("\nMobitel ostaje u vlasništvu firme.", fontNormal));
        tijelo.add(textTijela);
        tijelo.setIndentationLeft(20);
        tijelo.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(tijelo);

        Paragraph potpisnikRed1 = new Paragraph();
        Phrase red1 = new Phrase();
        red1.add(new Chunk("Primio sredstva", fontNormal));
        red1.add(new Chunk("                                                                  "));
        red1.add(new Chunk("Platitelj", fontNormal));
        potpisnikRed1.setAlignment(Element.ALIGN_CENTER);
        potpisnikRed1.add(red1);
        potpisnikRed1.setSpacingBefore(90);
        potpisnikRed1.setSpacingAfter(10);
        document.add(potpisnikRed1);

        Paragraph potpisnikRed2 = new Paragraph();
        Font fontPotpisnik = new Font(arialNormalFont, 9, Font.NORMAL);
        Phrase red2 = new Phrase();
        red2.add(new Chunk("_____________________________________", fontPotpisnik));
        red2.add(new Chunk("                             "));
        red2.add(new Chunk("_____________________________________ \n", fontPotpisnik));
        Phrase red3 = new Phrase();
        red3.add(new Chunk("            AITAC d.o.o.", fontPotpisnik));
        red3.add(new Chunk("                                                                 "));
        if(inventar.getKorisnik() != null){
            red3.add(new Chunk(inventar.getKorisnik().getFirstName() + " " + inventar.getKorisnik().getLastName(), fontPotpisnik));
        } else {
            red3.add(new Chunk(" ", fontPotpisnik));
        }
        potpisnikRed2.add(red2);
        potpisnikRed2.add(red3);
        potpisnikRed2.setSpacingBefore(70);
        potpisnikRed2.setSpacingAfter(20);
        potpisnikRed2.setAlignment(Element.ALIGN_CENTER);
        potpisnikRed2.setIndentationLeft(10);
        document.add(potpisnikRed2);

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
                .filename(filename+".pdf")
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



        Paragraph aitacPodaci1 = new Paragraph();
        Font fontBlueLine = new Font(arialNormalFont, 5, Font.BOLD);
        fontBlueLine.setColor(35, 47, 131);
        Chunk blueLine = new Chunk("_______________________________________________________________________________________________________________________________ \n", fontBlueLine);
        aitacPodaci1.add(blueLine);
        Font fontAitacPodaci = new Font(arialNormalFont, 6, Font.NORMAL);
        fontAitacPodaci.setColor(160, 160, 160);
        Phrase prviRed = new Phrase("AITAC d.o.o. Istarska cesta 1, 51215 Kastav, Hrvatska · T:+385 51 626 712 · F:+385 51 626 720 · E:info@aitac.nl · W: www.aitac.nl", fontAitacPodaci);
        aitacPodaci1.setAlignment(Element.ALIGN_CENTER);
        aitacPodaci1.add(prviRed);
        aitacPodaci1.setSpacingBefore(30);
        document.add(aitacPodaci1);

        Paragraph aitacPodaci2 = new Paragraph();
        Phrase drugiRed = new Phrase("Temeljni kapital: 18.000,00 kn uplaćen kod ZAP Rijeka, Hrvatska · Članovi uprave: M. Lorencin, MBS:040226601, Trgovački Sud u Rijeci", fontAitacPodaci);
        aitacPodaci2.setAlignment(Element.ALIGN_CENTER);
        aitacPodaci2.add(drugiRed);
        aitacPodaci2.add(blueLine);
        aitacPodaci2.setSpacingAfter(30);
        document.add(aitacPodaci2);

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
        Inventar inventarOne = selectedInventar.get(0);
        Chunk nazivKorisnika = new Chunk(inventarOne.getKorisnik().getFirstName() + " " + inventarOne.getKorisnik().getLastName(), boldFontKorisnika);
        nazivKorisnika.setUnderline(0.3f, -2f);
        Phrase selectedKorisnik = new Phrase(nazivKorisnika);
        podaciKorisnika.add(selectedKorisnik);
        document.add(podaciKorisnika);

        //Adding date of the report
        Paragraph printDate = new Paragraph();
        String datum = "Datum: ";
        String todayDate =  getToday().format(formatter);
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
        setCellContentAndFont(headerCell,"", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"INVENTARNI \n BROJ", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"NAZIV \n UREĐAJA", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"SERIJSKI \n BROJ", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"VRSTA \n UREĐAJA", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"DATUM \n ZADUŽENJA", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"DATUM \n RAZDUŽENJA", headerFont);
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
            if(inventar.getNazivUredaja() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getSerijskiBroj(), croatianFont);
            }
            table.addCell(cell);
            setCellContentAndFont(cell, inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont);
            table.addCell(cell);
            if(inventar.getDatumZaduzenja() == null){
                setCellContentAndFont(cell, "", croatianFont);
            } else {
                setCellContentAndFont(cell, inventar.getDatumZaduzenja().format(formatter), croatianFontBold);
            }
            table.addCell(cell);
            if(inventar.getDatumRazduzenja() == null){
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
                .filename("Inv"+filename+".pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
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
