package com.vranic.zavrsnirad.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.VrstaUredaja;
import com.vranic.zavrsnirad.service.VrstaUredajaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/vrstaUredaja")
public class VrstaUredajaController {
    @Autowired
    private VrstaUredajaService vrstaUredajaService;

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has admin role
            return "admin/vrstaUredaja";
        } else if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            return "vrstaUredaja/vrstaUredaja";
        }
        return "error";
    }

    @GetMapping("/all")
    public String getAllVrstaUredaja(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("sveVrsteUredaja", vrstaUredajaService.getAllVrstaUredaja());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/{id}")
    public VrstaUredaja getVrstaUredajaById(@PathVariable Long id) {
        return vrstaUredajaService.getVrstaUredajaById(id);
    }

    @GetMapping("/update/{id}")
    public String updateVrstaUredaja(@PathVariable(value = "id") Long id, Model model) throws Exception {
        VrstaUredaja vrstaUredaja = vrstaUredajaService.getVrstaUredajaById(id);
        model.addAttribute("vrstaUredaja", vrstaUredaja);
        return "vrstaUredaja/updateVrstaUredaja";
    }

    @GetMapping("/addNew")
    public String addNewVrstaUredaja(Model model) throws Exception {
        VrstaUredaja vrstaUredaja = new VrstaUredaja();
        model.addAttribute("vrstaUredaja", vrstaUredaja);
        return "vrstaUredaja/newVrstaUredaja";
    }

    @PostMapping("/addNew")
    public String addVrstaUredaja(@ModelAttribute("vrstaUredaja") VrstaUredaja vrstaUredaja, Model model) throws Exception {
        if(vrstaUredajaService.checkIfNazivVrsteUredajaIsAvailable(vrstaUredaja.getNazivVrsteUredaja())!=0){
            model.addAttribute("error", "Vrsta uređaja tog naziva već postoji!");
            return "vrstaUredaja/newVrstaUredaja";
        }else {
            vrstaUredajaService.save(vrstaUredaja);
        }
        return "redirect:/vrstaUredaja/all";
    }

    @PostMapping("/save")
    public String saveVrstaUredaja(@ModelAttribute("vrstaUredaja") VrstaUredaja vrstaUredaja, Model model) throws Exception{
        if(vrstaUredajaService.checkIfNazivVrsteUredajaIsAvailable(vrstaUredaja.getNazivVrsteUredaja())!=0)
        {
            model.addAttribute("error", "Vrsta uređaja tog naziva već postoji!");
            return "vrstaUredaja/updateVrstaUredaja";
        }
        vrstaUredajaService.save(vrstaUredaja);
        return "redirect:/vrstaUredaja/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id) {
        vrstaUredajaService.deleteById(id);
        return "redirect:/vrstaUredaja/all";
    }

    @GetMapping("/find")
    public String findVrstaUredajaByName(@RequestParam("nazivVrsteUredaja") String nazivVrsteUredaja, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<VrstaUredaja> vrstaUredajaList = vrstaUredajaService.findVrstaUredajaByName(nazivVrsteUredaja);
        if (vrstaUredajaList.isEmpty()) {
            model.addAttribute("error", "Vrsta uređaja tog naziva ne postoji u sustavu!");
            model.addAttribute("sveVrsteUredaja", vrstaUredajaService.getAllVrstaUredaja());
            VrstaUredaja vrstaUredaja = new VrstaUredaja();
            model.addAttribute("vrstaUredaja", vrstaUredaja);
        } else {
            model.addAttribute("sveVrsteUredaja", vrstaUredajaList);
            VrstaUredaja vrstaUredaja = new VrstaUredaja();
            model.addAttribute("vrstaUredaja", vrstaUredaja);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/generatePDF")
    public ResponseEntity<byte[]> generatePDF(HttpServletResponse response) throws Exception {
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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O VRSTAMA UREĐAJA", boldFont);
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

        // Create a table with 2 columns
        PdfPTable table = new PdfPTable(2);

        // Set the table width as a percentage of the available page width
        table.setWidthPercentage(50);

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
        headerCell.setPhrase(new Phrase("ID vrste uređaja", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Naziv vrste uređaja", headerFont));
        table.addCell(headerCell);

        // Get the list of Vrsta uredaja objects from service
        List<VrstaUredaja> vrsteUredaja = vrstaUredajaService.getAllVrstaUredaja();

        // Add data cells to the table
        for (VrstaUredaja vrstaUredaja : vrsteUredaja) {
            dataCell.setPhrase(new Phrase(String.valueOf(vrstaUredaja.getIdVrsteUredaja()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(vrstaUredaja.getNazivVrsteUredaja(), croatianFont));
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
                .filename("Vrste_uredaja-izvjestaj.pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
