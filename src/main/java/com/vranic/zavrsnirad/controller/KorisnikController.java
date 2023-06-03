package com.vranic.zavrsnirad.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.Korisnik;
import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.service.KorisnikService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
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

    @GetMapping("/generatePDFaktivni")
    public void generatePDF(HttpServletResponse response) throws IOException, DocumentException {
        // Set the content type and attachment header
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"korisnici-izvjestaj-aktivni.pdf\"");

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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O AKTIVNIM KORISNICIMA", boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20); // Adjust the value as per your requirement
        document.add(header);

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
        headerCell.setPhrase(new Phrase("Username", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Ime", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Prezime", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Status", headerFont));
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
        headerCell.setPhrase(new Phrase("Korisnik deaktiviran", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("E-mail aktiviran", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("E-mail deaktiviran", headerFont));
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
            dataCell.setPhrase(new Phrase(korisnik.getStatus(), croatianFont));
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
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getUserDisabled()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getEmailCreated()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getEmailDisabled()), croatianFont));
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
        float y = image.getScaledHeight() + document.bottomMargin(); // Position from the bottom

        image.setAbsolutePosition(x, y);
        document.add(image);
        // Close the document
        document.close();
    }

    @GetMapping("/generatePDFneaktivni")
    public void generatePDFneaktivni(HttpServletResponse response) throws IOException, DocumentException {
        // Set the content type and attachment header
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"korisnici-izvjestaj-neaktivni.pdf\"");

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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O NEAKTIVNIM KORISNICIMA", boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20); // Adjust the value as per your requirement
        document.add(header);

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
        headerCell.setPhrase(new Phrase("Username", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Ime", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Prezime", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Status", headerFont));
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
        headerCell.setPhrase(new Phrase("Korisnik deaktiviran", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("E-mail aktiviran", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("E-mail deaktiviran", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Komentar", headerFont));
        table.addCell(headerCell);

        // Get the list of Lokacija objects from service
        List<Korisnik> korisnici = korisnikService.getAllInactiveKorisnik();

        // Add data cells to the table
        for (Korisnik korisnik : korisnici) {
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getUsername()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getFirstName(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getLastName(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(korisnik.getStatus(), croatianFont));
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
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getUserDisabled()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getEmailCreated()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(korisnik.getEmailDisabled()), croatianFont));
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
        float y = image.getScaledHeight() + document.bottomMargin(); // Position from the bottom

        image.setAbsolutePosition(x, y);
        document.add(image);
        // Close the document
        document.close();
    }
}
