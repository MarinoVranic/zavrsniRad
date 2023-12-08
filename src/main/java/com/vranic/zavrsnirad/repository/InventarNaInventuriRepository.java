package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.InventarNaInventuri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarNaInventuriRepository extends JpaRepository<InventarNaInventuri, Long> {

    @Query(value = "SELECT inventarNaInventuri FROM InventarNaInventuri inventarNaInventuri LEFT JOIN FETCH inventarNaInventuri.inventar LEFT JOIN FETCH inventarNaInventuri.inventura ORDER BY inventarNaInventuri.inventar.inventarniBroj DESC")
    List<InventarNaInventuri> findAll();

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.Inventarni_broj = :inventarniBroj", nativeQuery = true)
    List<InventarNaInventuri> findByInvBroj(String inventarniBroj);

    @Query(value = "SELECT * FROM inventar_na_inventuri ini WHERE ini.ID_Inventure = :idInventure", nativeQuery = true)
    List<InventarNaInventuri> findByGodInventure(Long idInventure);

    @Query(value = "SELECT COUNT(ini.Inventarni_broj AND ini.ID_Inventure) FROM inventar_na_inventuri ini WHERE ini.Inventarni_broj = :inventarniBroj AND ini.ID_Inventure = :idInventure", nativeQuery = true)
    Long checkIfInvBrojIsScanned(String inventarniBroj, Long idInventure);
}
