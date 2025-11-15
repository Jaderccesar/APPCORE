package com.example.appcore.service;

import com.example.appcore.model.Certificate;
import com.example.appcore.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public List<Certificate> findAll() {
       return certificateRepository.findAll();
    }

    public Optional<Certificate> findById(Long id) {
        return certificateRepository.findById(id);
    }

    public Certificate save(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Certificate update(Long id, Certificate certificate) {
        Certificate existing = certificateRepository.findById(id).orElseThrow(() -> new RuntimeException("Certificado com id " + id + " não encontrado"));

        existing.setTitle(certificate.getTitle());
        existing.setDescription(certificate.getDescription());
        existing.setIssueDate(certificate.getIssueDate());
        existing.setExpiryDate(certificate.getExpiryDate());
        existing.setDocumentUrl(certificate.getDocumentUrl());

        return certificateRepository.save(existing);
    }

    public void delete(Long id) {
        certificateRepository.deleteById(id);
    }

}
