package com.vranic.zavrsnirad.util;

import com.vranic.zavrsnirad.model.Inventar;
import com.vranic.zavrsnirad.model.InventarNaInventuri;
import com.vranic.zavrsnirad.service.InventarNaInventuriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CsvGeneratorUtil {

    @Autowired
    private InventarNaInventuriService inventarNaInventuriService;
        private static final String CSV_HEADER_FOUND = "Inventura;Skenirani inventar;Naziv inventara;Vrsta inventara;Datum skeniranja;Upisana lokacija inventara;Skenirana lokacija inventara\n";
        private static final String CSV_HEADER_NOT_FOUND = "Inventarni broj;Naziv osnovnog sredstva;Vrsta osnovnog sredstva;Zaduženo na;Upisana lokacija osnovnog sredstva;Nabavna vrijednost\n";

        public String generateCsv(Long idInventure, String isFound, String tipInventara) {

            if(tipInventara.equals("SI")){
                if(isFound.equals("Pronađeno")){
                    List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.SIByGodinaInventure(idInventure);
                    StringBuilder csvContent = new StringBuilder();
                    csvContent.append(CSV_HEADER_FOUND);

                    for (InventarNaInventuri inventarNaInventuri : inventarNaInventuriList) {
                        csvContent.append(inventarNaInventuri.getInventura().getIdInventure().toString()).append(";")
                                .append(inventarNaInventuri.getInventar().getInventarniBroj()).append(";")
                                .append(inventarNaInventuri.getInventar().getNazivUredaja()).append(";")
                                .append(inventarNaInventuri.getInventar().getVrstaUredaja().getNazivVrsteUredaja()).append(";")
                                .append(inventarNaInventuri.getDatumSkeniranja().toLocalDate() + " " + inventarNaInventuri.getDatumSkeniranja().toLocalTime()).append(";")
                                .append(inventarNaInventuri.getInventar().getLokacija().getNazivLokacije()).append(";")
                                .append(inventarNaInventuri.getLokacija().getNazivLokacije()).append(";")
                                .append("\n");
                    }

                    return csvContent.toString();
                } else if(isFound.equals("Nepronađeno")){
                    List<Inventar> inventarList = inventarNaInventuriService.reportSIByInventuraAndNotFound(idInventure);
                    StringBuilder csvContent = new StringBuilder();
                    csvContent.append(CSV_HEADER_NOT_FOUND);

                    for (Inventar inventar : inventarList) {
                        csvContent.append(inventar.getInventarniBroj()).append(";")
                                .append(inventar.getNazivUredaja()).append(";")
                                .append(inventar.getVrstaUredaja().getNazivVrsteUredaja()).append(";");
                        if(inventar.getKorisnik() != null){
                            csvContent.append(inventar.getKorisnik().getFirstName() + " " + inventar.getKorisnik().getLastName()).append(";");
                        }else{
                            csvContent.append(" ").append(";");
                        }
                                csvContent.append(inventar.getLokacija().getNazivLokacije()).append(";")
                                        .append(String.format("%.2f", inventar.getNabavnaVrijednost())).append("\n");
                    }
                    return csvContent.toString();
                }
            } else if (tipInventara.equals("OS")){
                if(isFound.equals("Pronađeno")){
                    List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.OSByGodinaInventure(idInventure);
                    StringBuilder csvContent = new StringBuilder();
                    csvContent.append(CSV_HEADER_FOUND);

                    for (InventarNaInventuri inventarNaInventuri : inventarNaInventuriList) {
                        csvContent.append(inventarNaInventuri.getInventura().getIdInventure().toString()).append(";")
                                .append(inventarNaInventuri.getInventar().getInventarniBroj()).append(";")
                                .append(inventarNaInventuri.getInventar().getNazivUredaja()).append(";")
                                .append(inventarNaInventuri.getInventar().getVrstaUredaja().getNazivVrsteUredaja()).append(";")
                                .append(inventarNaInventuri.getDatumSkeniranja().toLocalDate() + " " + inventarNaInventuri.getDatumSkeniranja().toLocalTime()).append(";")
                                .append(inventarNaInventuri.getInventar().getLokacija().getNazivLokacije()).append(";")
                                .append(inventarNaInventuri.getLokacija().getNazivLokacije()).append(";")
                                .append("\n");
                    }
                    return csvContent.toString();
                } else if (isFound.equals("Nepronađeno")){
                    List<Inventar> inventarList = inventarNaInventuriService.reportOSByInventuraAndNotFound(idInventure);
                    StringBuilder csvContent = new StringBuilder();
                    csvContent.append(CSV_HEADER_NOT_FOUND);

                    for (Inventar inventar : inventarList) {
                        csvContent.append(inventar.getInventarniBroj()).append(";")
                                .append(inventar.getNazivUredaja()).append(";")
                                .append(inventar.getVrstaUredaja().getNazivVrsteUredaja()).append(";");
                        if(inventar.getKorisnik() != null){
                            csvContent.append(inventar.getKorisnik().getFirstName() + " " + inventar.getKorisnik().getLastName()).append(";");
                        }else{
                            csvContent.append(" ").append(";");
                        }
                        csvContent.append(inventar.getLokacija().getNazivLokacije()).append(";")
                                .append(String.format("%.2f", inventar.getNabavnaVrijednost())).append("\n");
                    }
                    return csvContent.toString();
                }
            }
            return "Error has occured!";
        }
    }
