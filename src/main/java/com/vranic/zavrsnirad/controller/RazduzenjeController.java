package com.vranic.zavrsnirad.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vranic.zavrsnirad.model.*;
import com.vranic.zavrsnirad.service.InventarService;
import com.vranic.zavrsnirad.service.KorisnikService;
import com.vranic.zavrsnirad.service.RazduzenjeService;
import com.vranic.zavrsnirad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/razduzenje")
public class RazduzenjeController {

    @Autowired
    private RazduzenjeService razduzenjeService;

    @Autowired
    private KorisnikService korisnikService;

    @Autowired
    private InventarService inventarService;

    @Autowired
    private UserService userService;

    public LocalDate getToday() {
        return LocalDate.now();
    }
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @GetMapping("/all")
    public String getAllRazduzenje(Model model) throws Exception {
        model.addAttribute("svaRazduzenja", razduzenjeService.getAllRazduzenje());
        model.addAttribute("allKorisnik", korisnikService.getAllKorisnik());
        return "razduzenje/razduzenje";
    }

    @GetMapping("/delete/{idRazduzenja}")
    public String deleteById(@PathVariable(value = "idRazduzenja") Long idRazduzenja){
        razduzenjeService.deleteById(idRazduzenja);
        return "redirect:/razduzenje/all";
    }

    @GetMapping("/find")
    public String findRazduzenjeByInventarniBroj(@RequestParam("inventarniBroj") String inventarniBroj, Model model) throws Exception {
        List<Razduzenje> razduzenjeList = razduzenjeService.getAllByInventarniBroj(inventarniBroj);
        if (razduzenjeList.isEmpty()) {
            model.addAttribute("error", "Inventar pod tim brojem ne postoji u sustavu!");
            model.addAttribute("svaRazduzenja", razduzenjeService.getAllRazduzenje());
            Razduzenje razduzenje = new Razduzenje();
            model.addAttribute("razduzenje", razduzenje);
        } else {
            model.addAttribute("svaRazduzenja", razduzenjeList);
            Razduzenje razduzenje = new Razduzenje();
            model.addAttribute("razduzenje", razduzenje);
        }
        return "razduzenje/razduzenje";
    }

    @GetMapping("/findByUser")
    public String showRazduzenjaByUser(@RequestParam("username") String username, Model model) throws Exception {
        List<Korisnik> allKorisnik = korisnikService.getAllKorisnik();
        List<Razduzenje> razduzenjeList = razduzenjeService.getAllByUser(username);
        model.addAttribute("allKorisnik", allKorisnik);
        model.addAttribute("svaRazduzenja", razduzenjeList);
        Razduzenje razduzenje = new Razduzenje();
        model.addAttribute("razduzenje", razduzenje);
        return "razduzenje/razduzenje";
    }

    @GetMapping("/printPDFrazduzenja")
    public ResponseEntity<byte[]> printPDFzaduzenja(@RequestParam("selectedItems") List<String> selectedItems) throws Exception {

        // Create a list for storing extracted inventarniBroj values
        List<String> inventarniBrojevi = new ArrayList<>();
        List<String> idjeviRazduzenja = new ArrayList<>();

        // Variable to store a single idRazduzenja (assuming all items have the same idRazduzenja)
        String selectedIdRazduzenja = null;

        // Process each selected item
        for (String selectedItem : selectedItems) {
            // Split the item into idRazduzenja and inventarniBroj
            String[] parts = selectedItem.split("\\|");
            selectedIdRazduzenja = parts[0];
            String currentInventarniBroj = parts[1];

            // Add the inventarniBroj to the list
            inventarniBrojevi.add(currentInventarniBroj);
            idjeviRazduzenja.add(selectedIdRazduzenja);

        }

        // Retrieve all selected objects
        List<Inventar> selectedInventar = inventarniBrojevi.stream()
                .map(inventarniBroj -> inventarService.getInventarById(inventarniBroj))
                .filter(inventar -> inventar != null)
                .toList();

        List<Razduzenje> selectedRazduzenje = idjeviRazduzenja.stream()
                .map(idRazduzenja -> razduzenjeService.getById(Long.valueOf(idRazduzenja)))
                .filter(razduzenje -> razduzenje != null)
                .toList();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        Razduzenje razduzenje = razduzenjeService.getById(Long.valueOf(selectedIdRazduzenja));

        Inventar inventarOne = inventarService.getInventarById(selectedInventar.get(0).getInventarniBroj());

        String filename = String.join("_", inventarniBrojevi);

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
            Phrase headerPhrase = new Phrase("Potvrda o razduženju službene opreme", boldFont);
            header.add(headerPhrase);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(75);
            header.setSpacingBefore(130);
            document.add(header);

            Paragraph podaciKorisnika = new Paragraph();
            Font fontImePrezime = new Font(arialNormalFont, 9, Font.NORMAL);
            Phrase imePrezime = new Phrase("Ime i Prezime: ", fontImePrezime);
            podaciKorisnika.add(imePrezime);
            podaciKorisnika.setAlignment(Element.ALIGN_LEFT);
            podaciKorisnika.setSpacingAfter(5);
            Font boldFontKorisnika = new Font(arialBoldFont, 13, Font.BOLD);
            Chunk nazivKorisnika = new Chunk(razduzenje.getKorisnik().getFirstName() + " " + razduzenje.getKorisnik().getLastName(), boldFontKorisnika);
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

            // Create a table with 6 columns
            PdfPTable table = new PdfPTable(6);

            // Set the table width as a percentage of the available page width
            table.setWidthPercentage(100);

            float firstColumnWidthPercentage = 5f; // Primjer postotka širine prvog stupca
            float remainingWidthPercentage = (100f - firstColumnWidthPercentage) / 5; // Preostali postotak za preostalih 5 stupaca
            float[] columnWidths = {firstColumnWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage};
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
            setCellContentAndFont(headerCell, "DATUM \n RAZDUŽENJA", headerFont);
            table.addCell(headerCell);

            int counter = 1;
            // Add data cells to the table
            for (Razduzenje razduzenje1 : selectedRazduzenje) {
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                setCellContentAndFont(cell, String.valueOf(counter), croatianFont);
                table.addCell(cell);
                counter++;
                setCellContentAndFont(cell, razduzenje1.getInventar().getInventarniBroj(), croatianFont);
                table.addCell(cell);
                setCellContentAndFont(cell, razduzenje1.getInventar().getNazivUredaja(), croatianFont);
                table.addCell(cell);
                if (razduzenje1.getInventar().getNazivUredaja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, razduzenje1.getInventar().getSerijskiBroj(), croatianFont);
                }
                table.addCell(cell);
                setCellContentAndFont(cell, razduzenje1.getInventar().getVrstaUredaja().getNazivVrsteUredaja(), croatianFont);
                table.addCell(cell);
                if (razduzenje1.getDatumRazduzenja() == null) {
                    setCellContentAndFont(cell, "", croatianFont);
                } else {
                    setCellContentAndFont(cell, razduzenje1.getDatumRazduzenja().format(formatter), croatianFont);
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
            redI.add(new Chunk ("Ovu potvrdu izdaje ", fontPravilnik));
            if(user!=null){
                redI.add(new Chunk(user.getFirstName().toUpperCase() + " " + user.getLastName().toUpperCase(), fontPravilnikBold));
            } else {
                redI.add(new Chunk(" ", fontPravilnikBold));
            }
            redI.add(new Chunk(" u ime poduzeća ", fontPravilnik));
            if(inventarOne.getCompany()!=null){
                redI.add(new Chunk(inventarOne.getCompany().getCompanyName().toUpperCase(), fontPravilnikBold));
            } else {
                redI.add(new Chunk(" ", fontPravilnikBold));
            }
            redI.add(new Chunk(" kao ovlaštena osoba za upravljanje i razduženje službene opreme. Dokument se smatra službenim i valjanim " +
                    "te obvezuje sve uključene strane.", fontPravilnik));
            redI.add(new Chunk("\n \n \n \n", fontPravilnik));
            Phrase redII = new Phrase();
            redII.add(new Chunk("Ovaj dokument je generiran digitalnim putem i valjan je bez vlastoručnog potpisa, " +
                    "sukladno zakonskim propisima o elektroničkom poslovanju i dokumentima. Svi podaci navedeni u " +
                    "ovom dokumentu smatraju se točnima i obvezujućima za sve uključene strane. \n", fontPravilnik));
            pravilnik.setAlignment(Element.ALIGN_LEFT);
            pravilnik.add(redI);
            pravilnik.add(redII);
            pravilnik.setSpacingBefore(60);
            document.add(pravilnik);

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
            //TO DO
//            Paragraph aitacPodaci1 = new Paragraph();
//            Font fontBlueLine = new Font(arialNormalFont, 5, Font.BOLD);
//            fontBlueLine.setColor(35, 47, 131);
//            Chunk blueLine = new Chunk("_______________________________________________________________________________________________________________________________ \n", fontBlueLine);
//            aitacPodaci1.add(blueLine);
//            Font fontAitacPodaci = new Font(arialNormalFont, 6, Font.NORMAL);
//            fontAitacPodaci.setColor(160, 160, 160);
//            Phrase prviRed = new Phrase("AITAC GMBH Istarska cesta 1, 51215 Kastav, Hrvatska · T:+385 51 626 712 · F:+385 51 626 720 · E:info@aitac.nl · W: www.aitac.nl", fontAitacPodaci);
//            aitacPodaci1.setAlignment(Element.ALIGN_CENTER);
//            aitacPodaci1.add(prviRed);
//            aitacPodaci1.setSpacingBefore(30);
//            document.add(aitacPodaci1);
//
//            Paragraph aitacPodaci2 = new Paragraph();
//            Phrase drugiRed = new Phrase("Temeljni kapital: 18.000,00 kn uplaćen kod ZAP Rijeka, Hrvatska · Članovi uprave: M. Lorencin, MBS:040226601, Trgovački Sud u Rijeci", fontAitacPodaci);
//            aitacPodaci2.setAlignment(Element.ALIGN_CENTER);
//            aitacPodaci2.add(drugiRed);
//            aitacPodaci2.add(blueLine);
//            aitacPodaci2.setSpacingAfter(30);
//            document.add(aitacPodaci2);
//
//            Paragraph header = new Paragraph();
//            Font boldFont = new Font(arialBoldItalicFont, 18, Font.BOLD);
//            Phrase headerPhrase = new Phrase("Zaduženja tijekom rada", boldFont);
//            header.add(headerPhrase);
//            header.setAlignment(Element.ALIGN_CENTER);
//            header.setSpacingAfter(75);
//            header.setSpacingBefore(50);
//            document.add(header);
//
//            Paragraph podaciKorisnika = new Paragraph();
//            Font fontImePrezime = new Font(arialNormalFont, 9, Font.NORMAL);
//            Phrase imePrezime = new Phrase("Ime i Prezime: ", fontImePrezime);
//            podaciKorisnika.add(imePrezime);
//            podaciKorisnika.setAlignment(Element.ALIGN_LEFT);
//            podaciKorisnika.setSpacingAfter(5);
//            Font boldFontKorisnika = new Font(arialBoldFont, 13, Font.BOLD);
//            Chunk nazivKorisnika = new Chunk(inventarOne.getKorisnik().getFirstName() + " " + inventarOne.getKorisnik().getLastName(), boldFontKorisnika);
//            nazivKorisnika.setUnderline(0.3f, -2f);
//            Phrase selectedKorisnik = new Phrase(nazivKorisnika);
//            podaciKorisnika.add(selectedKorisnik);
//            document.add(podaciKorisnika);
//
//            //Adding date of the report
//            Paragraph printDate = new Paragraph();
//            String datum = "Datum: ";
//            String todayDate = getToday().format(formatter);
//            Font datumFont = new Font(arialNormalFont, 9, Font.NORMAL);
//            Font dateFont = new Font(arialBoldFont, 10, Font.BOLD);
//            Phrase datumPhrase = new Phrase(datum, datumFont);
//            Phrase datePhrase = new Phrase(todayDate, dateFont);
//            printDate.add(datumPhrase);
//            printDate.add(datePhrase);
//            printDate.setAlignment(Element.ALIGN_LEFT);
//            printDate.setSpacingAfter(70);
//            document.add(printDate);
//
//            // Create a table with 15 columns
//            PdfPTable table = new PdfPTable(7);
//
//            // Set the table width as a percentage of the available page width
//            table.setWidthPercentage(100);
//
//            float firstColumnWidthPercentage = 5f; // Primjer postotka širine prvog stupca
//            float remainingWidthPercentage = (100f - firstColumnWidthPercentage) / 6; // Preostali postotak za preostalih 6 stupaca
//            float[] columnWidths = {firstColumnWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage, remainingWidthPercentage};
//            table.setWidths(columnWidths);
//
//            // Set the data cell style
//            PdfPCell dataCell = new PdfPCell();
//            dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//            // Set table header cell styles
//            Font headerFont = new Font(arialBoldFont, 12, Font.BOLD);
//            PdfPCell headerCell = new PdfPCell();
//            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            headerCell.setPadding(5);
//
//            // Add table header cells
//            setCellContentAndFont(headerCell, "", headerFont);
//            table.addCell(headerCell);
//            setCellContentAndFont(headerCell, "INVENTARNI \n BROJ", headerFont);
//            table.addCell(headerCell);
//            setCellContentAndFont(headerCell, "NAZIV \n UREĐAJA", headerFont);
//            table.addCell(headerCell);
//            setCellContentAndFont(headerCell, "SERIJSKI \n BROJ", headerFont);
//            table.addCell(headerCell);
//            setCellContentAndFont(headerCell, "VRSTA \n UREĐAJA", headerFont);
//            table.addCell(headerCell);
//            setCellContentAndFont(headerCell, "DATUM \n ZADUŽENJA", headerFont);
//            table.addCell(headerCell);
//            setCellContentAndFont(headerCell, "DATUM \n RAZDUŽENJA", headerFont);
//            table.addCell(headerCell);
//
//            int counter = 1;
//            // Add data cells to the table
//            for (Inventar inventar : selectedInventar) {
//                PdfPCell cell = new PdfPCell();
//                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//
//                setCellContentAndFont(cell, String.valueOf(counter), croatianFont);
//                table.addCell(cell);
//                counter++;
//                setCellContentAndFont(cell, inventar.getInventarniBroj(), croatianFont);
//                table.addCell(cell);
//                setCellContentAndFont(cell, inventar.getNazivUredaja(), croatianFont);
//                table.addCell(cell);
//                if (inventar.getNazivUredaja() == null) {
//                    setCellContentAndFont(cell, "", croatianFont);
//                } else {
//                    setCellContentAndFont(cell, inventar.getSerijskiBroj(), croatianFont);
//                }
//                table.addCell(cell);
//                setCellContentAndFont(cell, inventar.getVrstaUredaja().getNazivVrsteUredaja(), croatianFont);
//                table.addCell(cell);
//                if (inventar.getDatumZaduzenja() == null) {
//                    setCellContentAndFont(cell, "", croatianFont);
//                } else {
//                    setCellContentAndFont(cell, inventar.getDatumZaduzenja().format(formatter), croatianFontBold);
//                }
//                table.addCell(cell);
//                if (inventar.getDatumRazduzenja() == null) {
//                    setCellContentAndFont(cell, "", croatianFont);
//                } else {
//                    setCellContentAndFont(cell, inventar.getDatumRazduzenja().format(formatter), croatianFont);
//                }
//                table.addCell(cell);
//            }
//
//            // Add the table to the document
//            document.add(table);
//
//            Paragraph pravilnik = new Paragraph();
//            Font fontPravilnik = new Font(arialNormalFont, 9, Font.NORMAL);
//            Font fontPravilnikBold = new Font(arialBoldFont, 9, Font.BOLD);
//            Font fontPravilnikLink = new Font(arialNormalFont, 9, Font.BOLD);
//            fontPravilnikLink.setColor(35, 47, 131);
//            Phrase redI = new Phrase();
//            redI.add(new Chunk("Svojim potpisom korisnik potvrđuje da je upoznat te da će se pridržavati pravilnika o " +
//                    "korištenju informatičke opreme: \n", fontPravilnik));
//            Chunk boldChunk = new Chunk("QMS-POL-DOC-001-2  Politika pravilne uporabe informacijskih resursa \n", fontPravilnikBold);
//            redI.add(boldChunk);
//            redI.add(new Chunk("LINK: ", fontPravilnik));
//            Chunk linkChunk = new Chunk("\\\\osiris\\ISO\\AITAC ISO 9001\\15. POSLOVNE POLITIKE \n", fontPravilnikLink);
//            linkChunk.setUnderline(0.1f, -2f);
//            redI.add(linkChunk);
//            redI.add(new Chunk("\n"));
//            Phrase redII = new Phrase("Svojim potpisom potvrđujem da je sve navedeno zaduženo u dobrom stanju.", fontPravilnik);
//            pravilnik.setAlignment(Element.ALIGN_LEFT);
//            pravilnik.add(redI);
//            pravilnik.add(redII);
//            pravilnik.setSpacingBefore(60);
//            document.add(pravilnik);
//
//            Paragraph potpisnik = new Paragraph();
//            Font fontPotpisnik = new Font(arialNormalFont, 9, Font.NORMAL);
//            Phrase red1 = new Phrase();
//            red1.add(new Chunk("_____________________________________", fontPotpisnik));
//            red1.add(new Chunk("                              "));
//            red1.add(new Chunk("_____________________________________ \n", fontPotpisnik));
//            Phrase red2 = new Phrase();
//            red2.add(new Chunk("Zaposlenik", fontPotpisnik));
//            red2.add(new Chunk("                                                                      "));
//            red2.add(new Chunk("Odgovorna Osoba", fontPotpisnik));
//            potpisnik.setAlignment(Element.ALIGN_CENTER);
//            potpisnik.add(red1);
//            potpisnik.add(red2);
//            potpisnik.setSpacingBefore(90);
//            potpisnik.setSpacingAfter(20);
//            document.add(potpisnik);
//
//            //Adding another image and setting its size and position
//            String imagePath = "static/images/AitacLine.png"; // Relative path to the image file
//            Resource resource = new ClassPathResource(imagePath);
//            Image image = Image.getInstance(resource.getURL());
//
//            // Set the desired width and height of the image in centimeters
//            float desiredWidthInCm = 17f;
//            float desiredHeightInCm = 7f;
//
//            // Convert centimeters to points
//            float desiredWidthInPoints = desiredWidthInCm * 72 / 2.54f;
//            float desiredHeightInPoints = desiredHeightInCm * 72 / 2.54f;
//
//            // Set the desired width and height of the image in points
//            float desiredWidth = desiredWidthInPoints;
//            float desiredHeight = desiredHeightInPoints;
//            image.scaleToFit(desiredWidth, desiredHeight);
//
//            // Calculate the coordinates to position the image at the bottom
//            float x = (pageWidth - desiredWidth) / 2; // Centered horizontally
//            float y = document.bottomMargin() - image.getScaledHeight(); // Position from the bottom
//
//            image.setAbsolutePosition(x, y);
//            document.add(image);
//            // Close the document
//            document.close();
//
//            byte[] pdfContent = baos.toByteArray();
//            // Set HTTP headers for response
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_PDF);
//            headers.setContentDisposition(ContentDisposition.builder("attachment")
//                    .filename("Inv" + filename + ".pdf")
//                    .build());
//
//            // Return the PDF content as ResponseEntity
//            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
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
