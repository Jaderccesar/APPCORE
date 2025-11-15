package com.example.appcore.service;

import com.example.appcore.exception.ResourceNotFoundException;
import com.example.appcore.model.Enterprise;
import com.example.appcore.repository.EnterpriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnterpriseService {

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    public List<Enterprise> getEnterprises() {
        return enterpriseRepository.findAll();
    }

    public Optional<Enterprise> getEnterprise(Long id) {
        return enterpriseRepository.findById(id);
    }

    public Enterprise save(Enterprise enterprise) {
        return enterpriseRepository.save(enterprise);
    }

public Enterprise update(Long id, Enterprise enterprise) {
        Enterprise existing = enterpriseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa com o id " + id + " não encontrada"));

        existing.setName(enterprise.getName());
        existing.setEmail(enterprise.getEmail());
        existing.setPassword(enterprise.getPassword());
        existing.setBirthday(enterprise.getBirthday());
        existing.setGenero(enterprise.getGenero());
        existing.setFavoriteLanguage(enterprise.getFavoriteLanguage());
        existing.setAccountType(enterprise.getAccountType());
        existing.setStatus(enterprise.getStatus());
        existing.setFantasyName(enterprise.getFantasyName());
        existing.setCnpj(enterprise.getCnpj());

        return enterpriseRepository.save(existing);
}

    public void delete (Long id) {
        enterpriseRepository.deleteById(id);
    }
}
