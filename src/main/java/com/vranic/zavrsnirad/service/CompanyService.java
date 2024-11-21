package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.Company;
import com.vranic.zavrsnirad.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    public List<Company> getAllCompany() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public void save(Company company) {
        companyRepository.save(company);
    }

    public void deleteById(Long idCompany) {
        companyRepository.deleteById(idCompany);
    }

    public List<Company> findCompanyByName(String companyName) {
        return companyRepository.findByCompanyName(companyName);
    }

    public Long checkIfCompanyExists(String companyName) {
        return companyRepository.checkCompanyNameIsFree(companyName);
    }
}
