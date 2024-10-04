package com.vranic.zavrsnirad.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.service.KorisnikService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/korisnik")
public class KorisnikController {
    @Autowired
    private KorisnikService korisnikService;
    public LocalDate getToday() {
        return LocalDate.now();
    }

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "admin/korisnik";
        } else {
            // User has super_admin role
            return "korisnik/korisnik";
        }
    }

    @GetMapping("/all")
    public String getAllKorisnik(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/active")
    public String getAllActiveKorisnik(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("sviKorisnici", korisnikService.getAllActiveKorisnik());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/inactive")
    public String getAllInactiveKorisnik(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("sviKorisnici", korisnikService.getAllInactiveKorisnik());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/{username}")
    public Korisnik getKorisnikById(@PathVariable String username){
        return korisnikService.getKorisnikById(username);
    }

    @GetMapping("/update/{username}")
    public String updateKorisnik(@PathVariable(value = "username") String username, Model model) throws Exception{
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
    public String addNewKorisnik(Model model) throws Exception{
        Korisnik korisnik = new Korisnik();
        model.addAttribute("korisnik", korisnik);
        return "korisnik/newKorisnik";
    }


    @PostMapping("/addNew")
    public String addKorisnik(@ModelAttribute("korisnik") @Valid Korisnik korisnik, BindingResult bindingResult, Model model) throws Exception{
        String usernameFirstChar = korisnik.getUsername().substring(0,1).toUpperCase();
        String usernameSecondChar = korisnik.getUsername().substring(1,2).toUpperCase();
        String year = Integer.toString(getToday().getYear());

        if (bindingResult.hasErrors()) {
            return "korisnik/newKorisnik";
        } else if (korisnikService.checkIfUsernameIsFree(korisnik.getUsername()) != 0) {
            model.addAttribute("error", "Korisničko ime već postoji!");
            return "korisnik/newKorisnik";
        }

        // Set UserCreated and EmailCreated to today's date if not provided
        if (korisnik.getUserCreated() == null) {
            korisnik.setUserCreated(getToday());
        }
        if (korisnik.getEmailCreated() == null) {
            korisnik.setEmailCreated(getToday());
        }

        // Automatically generate email if not provided
        if (korisnik.getEmail().isEmpty()) {
            korisnik.setEmail(korisnik.getUsername() + "@aitac.nl");
        }

        // Automatically generate password if not provided
        if (korisnik.getInitialPassword().isEmpty()) {
            switch (korisnik.getAccountType()) {
                case "student", "praktikant" ->
                        korisnik.setInitialPassword(korisnikService.createPasswordStudent(usernameFirstChar, usernameSecondChar, getToday()));
                case "zaposlenik" ->
                        korisnik.setInitialPassword(korisnikService.createPasswordZaposlenik(usernameFirstChar, usernameSecondChar, getToday()));
                case "kooperant" ->
                        korisnik.setInitialPassword(korisnikService.createPasswordKooperant(usernameFirstChar, usernameSecondChar, getToday()));
            }
        }

        // Set year if not provided
        if (korisnik.getGodina().isEmpty()) {
            korisnik.setGodina(year);
        }

        // Save korisnik
        korisnikService.save(korisnik);

        return "redirect:/korisnik/all";
    }

    @PostMapping("/save")
    public String saveKorisnik(@ModelAttribute("korisnik") @Valid Korisnik korisnik, BindingResult bindingResult) throws Exception{
        if(bindingResult.hasErrors()){
            return "korisnik/updateKorisnik";
        }
            korisnikService.save(korisnik);
        return "redirect:/korisnik/all";
    }

    @GetMapping("deactivate/{username}")
    @Transactional
    public String deactivateKorisnik(@PathVariable(value = "username")String username) throws Exception {
        korisnikService.deactivateKorisnik(getToday(), getToday(), username);
        return "redirect:/korisnik/all";
    }

    @GetMapping("/find")
    public String findKorisnikByLastName(@RequestParam("lastName") String lastName, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Korisnik> korisnikList = korisnikService.findKorisnikByLastName(lastName);
        if (korisnikList.isEmpty()) {
            model.addAttribute("error", "Korisnik/ici tog prezimena nisu pronađeni!");
            model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
        } else {
            model.addAttribute("sviKorisnici", korisnikList);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findByFirstName")
    public String findKorisnikByFirstName(@RequestParam("firstName") String firstName, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Korisnik> korisnikList = korisnikService.findKorisnikByFirstName(firstName);
        if (korisnikList.isEmpty()) {
            model.addAttribute("error2", "Korisnik/ici tog imena nisu pronađeni!");
            model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
        } else {
            model.addAttribute("sviKorisnici", korisnikList);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/findBySubcontractor")
    public String findKorisnikBySubcontractor(@RequestParam("subcontractor") String subcontractor, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Korisnik> korisnikList = korisnikService.findKorisnikByPoslodavac(subcontractor);
        if (korisnikList.isEmpty()) {
            model.addAttribute("error3", "Traženi poslodavac nema korisnika!");
            model.addAttribute("sviKorisnici", korisnikService.getAllKorisnik());
        } else {
            model.addAttribute("sviKorisnici", korisnikList);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/generatePDFaktivni")
    public ResponseEntity<byte[]> generatePDF(HttpServletResponse response) throws Exception {
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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O AKTIVNIM KORISNICIMA", boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20); // Adjust the value as per your requirement
        document.add(header);

        //Adding date of the report
        Paragraph printDate = new Paragraph();
        LocalDate today = LocalDate.now();
        String todayDate = "Datum izvještaja: " + today;
        Font dateFont = new Font(arialBoldItalicFont, 10, Font.ITALIC);
        Phrase datePhrase = new Phrase(todayDate, dateFont);
        printDate.add(datePhrase);
        printDate.setAlignment(Element.ALIGN_LEFT);
        printDate.setSpacingAfter(5);
        document.add(printDate);

        // Create a table with 11 columns
        PdfPTable table = new PdfPTable(11);

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
        headerCell.setPhrase(new Phrase("Username", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Ime", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Prezime", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Vrsta korisnika", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Poslodavac", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Godina zaposlenja", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("E-mail adresa", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Inicijalna lozinka", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Korisnik aktiviran", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("E-mail aktiviran", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Komentar", headerFont));
        table.addCell(headerCell);

        // Get the list of Lokacija objects from service
        List<Korisnik> korisnici = korisnikService.getAllActiveKorisnik();

        // Add data cells to the table
        for (Korisnik korisnik : korisnici) {
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getUsername()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getFirstName(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getLastName(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getAccountType(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getSubcontractor(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getGodina()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getEmail(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getInitialPassword(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getUserCreated()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getEmailCreated()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getKomentar(), croatianFont));
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
                .filename("Korisnici-izvjestaj-aktivni.pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/generatePDFneaktivni")
    public ResponseEntity<byte[]> generatePDFneaktivni(HttpServletResponse response) throws Exception {
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
        String arialItalic = "/static/fonts/ariali.ttf";

        // Set the font for Croatian characters
        // Create the font using BaseFont.createFont
        BaseFont arialBoldFont = BaseFont.createFont(arialBold, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont arialBoldItalicFont = BaseFont.createFont(arialBoldItalic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font croatianFont = FontFactory.getFont(arialNormal, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont arialItalicFont = BaseFont.createFont(arialItalic, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O NEAKTIVNIM KORISNICIMA", boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20); // Adjust the value as per your requirement
        document.add(header);

        //Adding date of the report
        Paragraph printDate = new Paragraph();
        LocalDate today = LocalDate.now();
        String todayDate = "Datum izvještaja: " + today;
        Font dateFont = new Font(arialBoldItalicFont, 10, Font.ITALIC);
        Phrase datePhrase = new Phrase(todayDate, dateFont);
        printDate.add(datePhrase);
        printDate.setAlignment(Element.ALIGN_LEFT);
        printDate.setSpacingAfter(5);
        document.add(printDate);

        // Create a table with 14 columns
        PdfPTable table = new PdfPTable(14);

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
        setCellContentAndFont(headerCell, "Username", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Ime", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Prezime", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Status", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Vrsta korisnika", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Poslodavac", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Godina zaposlenja", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"E-mail adresa", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Inicijalna lozinka", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Korisnik aktiviran", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Korisnik deaktiviran", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"E-mail aktiviran", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"E-mail deaktiviran", headerFont);
        table.addCell(headerCell);
        setCellContentAndFont(headerCell,"Komentar", headerFont);
        table.addCell(headerCell);

        // Get the list of Lokacija objects from service
        List<Korisnik> korisnici = korisnikService.getAllInactiveKorisnik();

        // Add data cells to the table
        for (Korisnik korisnik : korisnici) {
            setCellContentAndFont(dataCell, String.valueOf(korisnik.getUsername()), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, korisnik.getFirstName(), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, korisnik.getLastName(), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, korisnik.getStatus(), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, korisnik.getAccountType(), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, korisnik.getSubcontractor(), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, String.valueOf(korisnik.getGodina()), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, korisnik.getEmail(), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, korisnik.getInitialPassword(), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, String.valueOf(korisnik.getUserCreated()), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, String.valueOf(korisnik.getUserDisabled()), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, String.valueOf(korisnik.getEmailCreated()), croatianFont);
            table.addCell(dataCell);
            setCellContentAndFont(dataCell, String.valueOf(korisnik.getEmailDisabled()), croatianFont);
            table.addCell(dataCell);
            if(korisnik.getKomentar() != null){
                setCellContentAndFont(dataCell, korisnik.getKomentar(), croatianFont);
            } else {
                setCellContentAndFont(dataCell, "", croatianFont);
            }
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
                .filename("Korisnici-izvjestaj-neaktivni.pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    @GetMapping("/printPDFzaduzenja")
    public ResponseEntity<byte[]> printPDFzaduzenja(@RequestParam("selectedItems") List<String> selectedItems, HttpServletResponse response) throws Exception {
        // Get the list of Korisnik objects from service
        List<Korisnik> selectedKorisnikList = selectedItems.stream()
                .map(username -> korisnikService.getKorisnikById(username))
                .filter(korisnik -> korisnik != null)
                .toList();

        Korisnik selectedKorisnik = selectedKorisnikList.get(0);

        String filename = selectedKorisnik.getFirstName()
                        .replaceAll("Ć", "C")
                        .replaceAll("Č", "C")
                        .replaceAll("Š", "S")
                        .replaceAll("Đ", "D")
                        .replaceAll("Ž", "Z")
                        .replaceAll("ć", "c")
                        .replaceAll("č", "c")
                        .replaceAll("š", "s")
                        .replaceAll("đ", "d")
                        .replaceAll("ž", "z")
                + " " +
                selectedKorisnik.getLastName()
                        .replaceAll("Ć", "C")
                        .replaceAll("Č", "C")
                        .replaceAll("Š", "S")
                        .replaceAll("Đ", "D")
                        .replaceAll("Ž", "Z")
                        .replaceAll("ć", "c")
                        .replaceAll("č", "c")
                        .replaceAll("š", "s")
                        .replaceAll("đ", "d")
                        .replaceAll("ž", "z");

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
        Font fontLink = new Font(arialNormalFont, 5, Font.UNDERLINE);
        fontLink.setColor(35, 47, 131);
        BaseColor aitacColor = new BaseColor(35, 47, 131);
        BaseColor blueCellColor = new BaseColor(0, 112, 192);
        BaseColor lightGreyCellColor = new BaseColor(224, 224, 224);

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
        fontBlueLine.setColor(aitacColor);
        Chunk blueLine = new Chunk("_______________________________________________________________________________________________________________________________ \n", fontBlueLine);
        aitacPodaci1.add(blueLine);
        Font fontAitacPodaci = new Font(arialNormalFont, 6, Font.NORMAL);
        fontAitacPodaci.setColor(160, 160, 160);
        Phrase prviRed = new Phrase("AITAC d.o.o. Žegoti 6/1, 51215 Kastav, Hrvatska · T:+385 51 626 712 · F:+385 51 626 720 · E:info@aitac.nl · W: www.aitac.nl", fontAitacPodaci);
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
        Phrase headerPhrase = new Phrase("Pristupni podaci za rad", boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(75);
        header.setSpacingBefore(50);
        document.add(header);

        // Create a table with 15 columns
        PdfPTable table = new PdfPTable(2);

        // Set the table width as a percentage of the available page width
        table.setWidthPercentage(80);

        float[] columnWidths = {30f, 70f};
        table.setWidths(columnWidths);

        // Set table header cell styles
        Font headerFont = new Font(arialBoldFont, 12, Font.BOLD);
        PdfPCell headerCell = new PdfPCell();
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setVerticalAlignment(Element.ALIGN_CENTER);
        headerCell.setBackgroundColor(blueCellColor);
        headerCell.setFixedHeight(30);
        headerCell.setPadding(5);
        headerCell.setColspan(2);

        // Add table header cells
        setCellContentAndFontLarge(headerCell,"Windows korisnički račun", headerFont);
        table.addCell(headerCell);

        // Add data cells to the table
        for (Korisnik korisnik : selectedKorisnikList) {
            PdfPCell leftCell = new PdfPCell();
            PdfPCell rightCell = new PdfPCell();
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            leftCell.setVerticalAlignment(Element.ALIGN_CENTER);
            leftCell.setBackgroundColor(blueCellColor);
            rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            rightCell.setVerticalAlignment(Element.ALIGN_CENTER);
            leftCell.setFixedHeight(20);
            rightCell.setFixedHeight(20);
            setCellContentAndFontLarge(leftCell, "Korisnik", croatianFontBold);
            table.addCell(leftCell);
            setCellContentAndFontLarge(rightCell, korisnik.getFirstName() + " " + korisnik.getLastName(), croatianFont);
            table.addCell(rightCell);
            setCellContentAndFontLarge(leftCell, "Korisničko ime", croatianFontBold);
            table.addCell(leftCell);
            setCellContentAndFontLarge(rightCell, korisnik.getUsername(), croatianFont);
            table.addCell(rightCell);
            setCellContentAndFontLarge(leftCell, "Lozinka", croatianFontBold);
            table.addCell(leftCell);
            setCellContentAndFontLarge(rightCell, korisnik.getInitialPassword(), croatianFont);
            table.addCell(rightCell);
        }

        Phrase phraseNapomena = new Phrase();
        Chunk chunkBold = new Chunk("NAPOMENA: ", headerFont);
        phraseNapomena.add(chunkBold);
        Font croatianFontRed = new Font(croatianFont);
        croatianFontRed.setColor(BaseColor.RED);
        Chunk chunkRed = new Chunk("Lozinka je identična za pristup svim servisima", croatianFontRed);
        phraseNapomena.add(chunkRed);
        PdfPCell lowerHeaderCell = new PdfPCell(phraseNapomena);
        lowerHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        lowerHeaderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        lowerHeaderCell.setFixedHeight(30);
        lowerHeaderCell.setColspan(2);
        table.addCell(lowerHeaderCell);

        // Create an empty cell
        PdfPCell emptyCell = new PdfPCell();
        // Set the border width to 0
        emptyCell.setBorderWidth(0);
        // Set the height of the empty cell
        emptyCell.setFixedHeight(10); // Adjust the height as needed
        emptyCell.setColspan(2);
        // Add the empty cell to the table
        table.addCell(emptyCell);

        for (Korisnik korisnik : selectedKorisnikList) {
            PdfPCell leftCell = new PdfPCell();
            PdfPCell rightCell = new PdfPCell();
            leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            leftCell.setVerticalAlignment(Element.ALIGN_CENTER);
            leftCell.setBackgroundColor(lightGreyCellColor);
            rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            rightCell.setVerticalAlignment(Element.ALIGN_CENTER);
            leftCell.setFixedHeight(20);
            rightCell.setFixedHeight(20);
            setCellContentAndFontLarge(leftCell, "Status", croatianFont);
            table.addCell(leftCell);
            setCellContentAndFontLarge(rightCell, korisnik.getAccountType(), croatianFont);
            table.addCell(rightCell);
            setCellContentAndFontLarge(leftCell, "E-mail adresa", croatianFont);
            table.addCell(leftCell);
            setCellContentAndFontLarge(rightCell, korisnik.getEmail(), croatianFont);
            table.addCell(rightCell);
            setCellContentAndFontLarge(leftCell, "E-mail login", croatianFont);
            table.addCell(leftCell);
            setCellContentAndFontLarge(rightCell, "https://www.office.com", fontLink);
            table.addCell(rightCell);
            setCellContentAndFontLarge(leftCell, "Odoo login", croatianFont);
            table.addCell(leftCell);
            setCellContentAndFontLarge(rightCell, "https://odoo.aitac.nl", fontLink);
            table.addCell(rightCell);
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
                .filename("Printout-"+filename+".pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

    private void setCellContentAndFont(PdfPCell cell, String content, Font font) {
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

    private void setCellContentAndFontLarge(PdfPCell cell, String content, Font font) {
        // Set initial font size
        float fontSize = 18f;
        float minFontSize = 12f; // Minimum font size for readability

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
