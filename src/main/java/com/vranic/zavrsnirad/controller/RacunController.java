package com.vranic.zavrsnirad.controller;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.model.Racun;
import com.vranic.zavrsnirad.service.FileService;
import com.vranic.zavrsnirad.service.RacunService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/racun")
public class RacunController {
    @Autowired
    private RacunService racunService;

    @Autowired
    private FileService fileService;

    private String getViewBasedOnRole(Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // User has the admin role
            return "admin/racun";
        } else {
            // User has super_admin role
            return "racun/racun";
        }
    }

    @GetMapping("/all")
    public String getAllRacun(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("sviRacuni", racunService.getAllRacun());
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/{id}")
    public Racun getRacunById(@PathVariable Long id) {
        return racunService.getRacunById(id);
    }

    @GetMapping("/update/{id}")
    public String updateRacun(@PathVariable(value = "id") Long id, Model model) {
        Racun racun = racunService.getRacunById(id);
        model.addAttribute("racun", racun);
        return "racun/updateRacun";
    }

    @GetMapping("/addNew")
    public String addNewRacun(Model model){
        Racun racun = new Racun();
        model.addAttribute("racun", racun);
        return "racun/newRacun";
    }

    @PostMapping("/addNew")
    public String addRacun(@ModelAttribute("racun") Racun racun, @RequestParam("datoteka") MultipartFile file, Model model) throws IOException {
        if(racunService.checkIfBrojRacunaIsAvailable(racun.getBrojRacuna()) != 0){
            model.addAttribute("error", "Broj računa/dokumenta već postoji!");
            return "racun/newRacun";
        } else {
            Long fileId = fileService.uploadFileReturnId(file);
            racun.setFile(fileService.getFileById(fileId));
            racunService.save(racun);
        }
        return "redirect:/racun/all";
    }

    @PostMapping("/save")
    public String saveRacun(@ModelAttribute("racun") Racun racun, Model model) {
        if(racunService.checkIfBrojRacunaIsAvailable(racun.getBrojRacuna())!=0)
        {
            model.addAttribute("error", "Broj računa/dokumenta već postoji!");
            return "racun/updateRacun";
        }
        racunService.save(racun);
        return "redirect:/racun/all";
    }

    @GetMapping("delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id) {
        racunService.deleteById(id);
        return "redirect:/racun/all";
    }

    @GetMapping("/find")
    public String findDobavljacByName(@RequestParam("brojRacuna") String brojRacuna, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Racun> racunList = racunService.findRacunByBrojRacuna(brojRacuna);
        if (racunList.isEmpty()) {
            model.addAttribute("error", "Račun/dokument pod tim brojem ne postoji u sustavu!");
            model.addAttribute("sviRacuni", racunService.getAllRacun());
            Racun racun = new Racun();
            model.addAttribute("racun", racun);
        } else {
            model.addAttribute("sviRacuni", racunList);
            Racun racun = new Racun();
            model.addAttribute("racun", racun);
        }
        return getViewBasedOnRole(auth);
    }

    @GetMapping("/generatePDF")
    public void generatePDF(HttpServletResponse response) throws IOException, DocumentException {
        // Set the content type and attachment header
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"racuni-izvjestaj.pdf\"");

        // Create a new PDF document
        Document document = new Document(PageSize.A4);

        // Create a PdfWriter instance to write the document to the response output stream
        PdfWriter.getInstance(document, response.getOutputStream());

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
        Phrase headerPhrase = new Phrase("IZVJEŠTAJ O RAČUNIMA/DOKUMENTIMA", boldFont);
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
        PdfPTable table = new PdfPTable(3);

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
        headerCell.setPhrase(new Phrase("ID računa/dokumenta", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Naziv računa/dokumenta", headerFont));
        table.addCell(headerCell);
        headerCell.setPhrase(new Phrase("Datum računa/dokumenta", headerFont));
        table.addCell(headerCell);

        // Get the list of Racun objects from service
        List<Racun> racuni = racunService.getAllRacun();

        // Add data cells to the table
        for (Racun racun : racuni) {
            dataCell.setPhrase(new Phrase(String.valueOf(racun.getIdRacuna()), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(racun.getBrojRacuna(), croatianFont));
            table.addCell(dataCell);
            dataCell.setPhrase(new Phrase(String.valueOf(racun.getDatumRacuna()), croatianFont));
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
