package com.vranic.zavrsnirad.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.service.DobavljacService;
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
@RequestMapping("/dobavljac")
public class DobavljacController {
    @Autowired
    private DobavljacService dobavljacService;

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "admin/dobavljac";
        } else {
            // User has user role
            return "dobavljac/dobavljac";
        }
    }

    @GetMapping("/all")
    public String getAllDobavljac(Model model) throws Exception{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("sviDobavljaci", dobavljacService.getAllDobavljaci());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/{id}")
    public Dobavljac getDobavljacById(@PathVariable Long id) {
        return dobavljacService.getDobavljacById(id);
    }

    @GetMapping("/update/{id}")
    public String updateDobavljac(@PathVariable(value = "id") Long id, Model model) throws Exception{
        Dobavljac dobavljac = dobavljacService.getDobavljacById(id);
        model.addAttribute("dobavljac", dobavljac);
        return "dobavljac/updateDobavljac";
    }

    @GetMapping("/addNew")
    public String addNewDobavljac(Model model) throws Exception{
        Dobavljac dobavljac = new Dobavljac();
        model.addAttribute("dobavljac", dobavljac);
        return "dobavljac/newDobavljac";
    }

    @PostMapping("/addNew")
    public String addDobavljac(@ModelAttribute("dobavljac") Dobavljac dobavljac, Model model) throws Exception{
        if(dobavljacService.checkIfNazivDobavljacaIsAvailable(dobavljac.getNazivDobavljaca())!=0){
            model.addAttribute("error", "Dobavljač tog naziva već postoji!");
            return "dobavljac/newDobavljac";
        }else {
            dobavljacService.save(dobavljac);
        }
        return "redirect:/dobavljac/all";
    }

    @PostMapping("/save")
    public String saveDobavljac(@ModelAttribute("dobavljac") Dobavljac dobavljac, Model model) throws Exception{
        if(dobavljacService.checkIfNazivDobavljacaIsAvailable(dobavljac.getNazivDobavljaca())!=0)
        {
            model.addAttribute("error", "Dobavljač tog naziva već postoji!");
            return "dobavljac/updateDobavljac";
        }
        dobavljacService.save(dobavljac);
        return "redirect:/dobavljac/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id) {
        dobavljacService.deleteById(id);
        return "redirect:/dobavljac/all";
    }

    @GetMapping("/find")
    public String findDobavljacByName(@RequestParam("nazivDobavljaca") String nazivDobavljaca, Model model) throws Exception{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Dobavljac> dobavljacList = dobavljacService.findDobavljacByName(nazivDobavljaca);
        if (dobavljacList.isEmpty()) {
            model.addAttribute("error", "Dobavljač tog naziva ne postoji u sustavu!");
            model.addAttribute("sviDobavljaci", dobavljacService.getAllDobavljaci());
            Dobavljac dobavljac = new Dobavljac();
            model.addAttribute("dobavljac", dobavljac);
        } else {
            model.addAttribute("sviDobavljaci", dobavljacList);
            Dobavljac dobavljac = new Dobavljac();
            model.addAttribute("dobavljac", dobavljac);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/generatePDF")
    public ResponseEntity<byte[]> generatePDF(HttpServletResponse response) throws IOException, DocumentException {
        // Set the content type and attachment header
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=\"dobavljaci-izvjestaj.pdf\"");

        // Create a new PDF document
        Document document = new Document(PageSize.A4);

        // Create a PdfWriter instance to write the document to the response output stream
//        PdfWriter.getInstance(document, response.getOutputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        // Open the document
        document.open();

        //Paths to arial fonts
        String arialBold = "/static/fonts/arialbd.ttf";
        String arialNormal = "/static/fonts/arial.ttf";
        String arialBoldItalic = "/static/fonts/arialbi.ttf";
        String arialBoldNarrow = "/static/fonts/ARIALNB.TTF";

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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O DOBAVLJAČIMA", boldFont);
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
        headerCell.setPhrase(new Phrase("ID dobavljača", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Naziv dobavljača", headerFont));
        table.addCell(headerCell);

        // Get the list of Dobavljac objects from service
        List<Dobavljac> dobavljaci = dobavljacService.getAllDobavljaci();

        // Add data cells to the table
        for (Dobavljac dobavljac : dobavljaci) {
            dataCell.setPhrase(new Phrase(String.valueOf(dobavljac.getIdDobavljaca()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(dobavljac.getNazivDobavljaca(), croatianFont));
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
                .filename("Izvjestaj_dobavljaci.pdf")
                .build());

        // Return the PDF content as ResponseEntity
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
