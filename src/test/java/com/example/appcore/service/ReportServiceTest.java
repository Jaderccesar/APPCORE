package com.example.appcore.service;

import com.example.appcore.model.Certificate;
import com.example.appcore.model.Course;
import com.example.appcore.model.Student;
import com.example.appcore.repository.CertificateRepository;
import com.example.appcore.service.ReportService.ClassroomReport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private ReportService reportService;

    private Student student;

    @BeforeEach
    void setup() {
        student = new Student();
        student.setId(1L);
        student.setName("John Doe");
    }

    // --------------------------------------------------------------------
    // RT01 - Gerar relatório individual
    // --------------------------------------------------------------------

    @Test
    void relatorioGeradoSucesso() {
        // CT01

        Certificate c1 = new Certificate();
        c1.setStudent(student);
        c1.setIssueDate(LocalDate.now().minusDays(5));

        Certificate c2 = new Certificate();
        c2.setStudent(student);
        c2.setIssueDate(LocalDate.now().minusDays(2));

        when(certificateRepository.findAll()).thenReturn(List.of(c1, c2));

        ReportService.UserPerformanceReport report =
                reportService.generateUserReport(1L, null);

        assertNotNull(report);
        assertEquals(2, report.certificates.size(), "Usuário possui 2 certificados");
        assertEquals("OK", report.status);
    }

    @Test
    void relatorioSemUsuarioValido() {
        // CT02

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.generateUserReport(0L, null)
        );

        assertEquals("ID de usuário inválido", ex.getMessage());
    }

    @Test
    void relatorioGeradoSemDados() {
        // CT03

        when(certificateRepository.findAll()).thenReturn(Collections.emptyList());

        ReportService.UserPerformanceReport report =
                reportService.generateUserReport(1L, null);

        assertNotNull(report);
        assertTrue(report.certificates.isEmpty(), "Lista de certifcados deve estar vazia");
        assertEquals("Sem dados de desempenho", report.status);
    }

    @Test
    void relatorioUtilizandoFiltros() {
        // CT04

        Certificate recentCert = new Certificate();
        recentCert.setStudent(student);
        recentCert.setIssueDate(LocalDate.now());

        Certificate oldCert = new Certificate();
        oldCert.setStudent(student);
        oldCert.setIssueDate(LocalDate.now().minusDays(5));

        when(certificateRepository.findAll()).thenReturn(List.of(recentCert, oldCert));

        ReportService.UserPerformanceReport report =
                reportService.generateUserReport(1L, 1);  // Intervalo de 1 dia

        assertNotNull(report);
        assertEquals(1, report.certificates.size(), "Somete 1 certificado recente");
        assertEquals("OK", report.status);
    }

}
