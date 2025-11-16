package com.example.appcore.service;

import com.example.appcore.model.Certificate;
import com.example.appcore.model.Course;
import com.example.appcore.model.Student;
import com.example.appcore.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public boolean verificarElegibilidadeCertificado(int progresso, Student aluno, Course curso) {
        return progresso == 100;
    }

    public boolean gerarCertificado(int progresso, Student aluno, Course curso) {

        //aqui so se for legivel
        if (!verificarElegibilidadeCertificado(progresso, aluno, curso)) {
            return false;
        }

        Certificate cert = new Certificate();
        cert.setStudent(aluno);
        cert.setCourse(curso);
        cert.setIssueDate(LocalDate.now());
        cert.setVerificationCode(UUID.randomUUID().toString());
        cert.setTitle("Certificado de Conclusão - " + curso.getTitle());
        cert.setDescription("Certificado emitido para o aluno " + aluno.getName());
        cert.setExpiryDate(null);
        cert.setDocumentUrl(null);

        certificateRepository.save(cert);
        return true;
    }
}
