package com.vranic.zavrsnirad.controller;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.vranic.zavrsnirad.model.Lokacija;
import com.vranic.zavrsnirad.service.LokacijaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.layout.element.Text;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/lokacija")
public class LokacijaController {
    @Autowired
    private LokacijaService lokacijaService;

    @GetMapping("/all")
    public String getAllLokacija(Model model) {
        model.addAttribute("sveLokacije", lokacijaService.getAllLokacija());
        return "lokacija/lokacija";
    }

    @GetMapping("/{id}")
    public Lokacija getLokacijaById(@PathVariable Long id) {
        return lokacijaService.getLokacijaById(id);
    }

    @GetMapping("/update/{id}")
    public String updateLokacija(@PathVariable(value = "id") Long id, Model model) {
        Lokacija lokacija = lokacijaService.getLokacijaById(id);
        model.addAttribute("lokacija", lokacija);
        return "lokacija/updateLokacija";
    }

    @GetMapping("/addNew")
    public String addNewLokacija(Model model){
        Lokacija lokacija = new Lokacija();
        model.addAttribute("lokacija", lokacija);
        return "lokacija/newLokacija";
    }

    @PostMapping("/addNew")
    public String addLokacija(@ModelAttribute("lokacija") Lokacija lokacija, Model model) {
        if(lokacijaService.checkIfNazivLokacijeIsAvailable(lokacija.getNazivLokacije())!=0){
            model.addAttribute("error", "Naziv lokacije već postoji!");
            return "lokacija/newLokacija";
        }else {
            lokacijaService.save(lokacija);
        }
        return "redirect:/lokacija/all";
    }

    @PostMapping("/save")
    public String saveLokacija(@ModelAttribute("lokacija") Lokacija lokacija, Model model) {
        if(lokacijaService.checkIfNazivLokacijeIsAvailable(lokacija.getNazivLokacije())!=0)
        {
            model.addAttribute("error", "Naziv lokacije već postoji!");
            return "lokacija/updateLokacija";
        }
        lokacijaService.save(lokacija);
        return "redirect:/lokacija/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id) {
        lokacijaService.deleteById(id);
        return "redirect:/lokacija/all";
    }

    @GetMapping("/find")
    public String findDobavljacByName(@RequestParam("nazivLokacije") String nazivLokacije, Model model) {
        List<Lokacija> lokacijaList = lokacijaService.findLokacijaByName(nazivLokacije);
        if (lokacijaList.isEmpty()) {
            model.addAttribute("error", "Lokacija tog naziva ne postoji u sustavu!");
            model.addAttribute("sveLokacije", lokacijaService.getAllLokacija());
            Lokacija lokacija = new Lokacija();
            model.addAttribute("lokacija", lokacija);
        } else {
            model.addAttribute("sveLokacije", lokacijaList);
            Lokacija lokacija = new Lokacija();
            model.addAttribute("lokacija", lokacija);
        }
        return "lokacija/lokacija";
    }

    @GetMapping("/generatePDF")
    public void generatePDF(HttpServletResponse response) throws IOException, DocumentException {
        // Set the content type and attachment header
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"lokacije.pdf\"");

        // Create a new PDF document
        Document document = new Document(PageSize.A4);

        // Create a PdfWriter instance to write the document to the response output stream
        PdfWriter.getInstance(document, response.getOutputStream());

        // Open the document
        document.open();

//        // Create the header text
//        Phrase headerText = new Phrase("Izvještaj o lokacijama", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
//
//        // Create the header paragraph
//        Paragraph header = new Paragraph(headerText);
//        document.add(header);
        Paragraph header = new Paragraph();
        Phrase headerPhrase = new Phrase("Izvještaj o lokacijama");
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        headerPhrase.setFont(boldFont);
        header.add(headerPhrase);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20); // Adjust the value as per your requirement
        document.add(header);
        // Set the font for Croatian characters
        Font croatianFont = FontFactory.getFont("/static/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Create a table with 2 columns
        PdfPTable table = new PdfPTable(2);

        // Set the table width as a percentage of the available page width
        table.setWidthPercentage(50);

        // Set the data cell style
        PdfPCell dataCell = new PdfPCell();
        dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Set table header cell styles
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        headerFont.setColor(BaseColor.WHITE);
        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(5);

        // Add table header cells
        headerCell.setPhrase(new Phrase("ID lokacije", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Naziv lokacije", headerFont));
        table.addCell(headerCell);
//        headerCell.setPhrase(new Phrase("", headerFont));
//        table.addCell(headerCell);
//        headerCell.setPhrase(new Phrase("", headerFont));
//        table.addCell(headerCell);

        // Get the list of Lokacija objects from your service
        List<Lokacija> lokacije = lokacijaService.getAllLokacija();

        // Add data cells to the table
        for (Lokacija lokacija : lokacije) {
            dataCell.setPhrase(new Phrase(String.valueOf(lokacija.getIdLokacije()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(lokacija.getNazivLokacije(), croatianFont));
            table.addCell(dataCell);
        }

        // Add the table to the document
        document.add(table);

        String imagePath = "static/images/AITAC values challenges solutions (1).png"; // Relative path to the image file
        Resource resource = new ClassPathResource(imagePath);
        Image image = Image.getInstance(resource.getURL());
        // Set the desired width and height of the image in points (1 point = 1/72 inch)
        float desiredWidth = 200f;
        float desiredHeight = 100f;
        image.scaleToFit(desiredWidth, desiredHeight);
        document.add(image);

        // Close the document
        document.close();
    }


}
