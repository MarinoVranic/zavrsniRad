package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value = "SELECT * FROM company c ORDER BY c.id_company ASC", nativeQuery = true)
    List<Company> findAllCompany();

    @Query(value = "SELECT company FROM Company company WHERE company.companyName LIKE CONCAT('%', :companyName, '%')")
    List<Company> findByCompanyName(String companyName);

    @Query(value = "SELECT COUNT(c.company_name) FROM company c WHERE c.company_name = :companyName", nativeQuery = true)
    Long checkCompanyNameIsFree(String companyName);
}
