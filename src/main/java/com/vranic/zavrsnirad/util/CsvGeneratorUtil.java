package com.vranic.zavrsnirad.util;

import com.vranic.zavrsnirad.model.InventarNaInventuri;
import com.vranic.zavrsnirad.service.InventarNaInventuriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CsvGeneratorUtil {

    @Autowired
    private InventarNaInventuriService inventarNaInventuriService;
        private static final String CSV_HEADER = "Inventura,Skenirani inventar,Naziv inventara,Vrsta inventara,Datum skeniranja\n";

        public String generateCsv(Long idInventure) {
            List<InventarNaInventuri> inventarNaInventuriList = inventarNaInventuriService.finbByGodinaInventure(idInventure);
            StringBuilder csvContent = new StringBuilder();
            csvContent.append(CSV_HEADER);

            for (InventarNaInventuri inventarNaInventuri : inventarNaInventuriList) {
                csvContent.append(inventarNaInventuri.getInventura().getIdInventure().toString()).append(",")
                            .append(inventarNaInventuri.getInventar().getInventarniBroj()).append(",")
                            .append(inventarNaInventuri.getInventar().getNazivUredaja()).append(",")
                            .append(inventarNaInventuri.getInventar().getVrstaUredaja().getNazivVrsteUredaja()).append(",")
                            .append(inventarNaInventuri.getDatumSkeniranja()).append("\n");
            }

            return csvContent.toString();
        }
    }
