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

    // --------------------------------------------------------------------
    // RT02 - Gerar relatório consolidado (professor/admin)
    // --------------------------------------------------------------------
    @Test
    void GerarRelatorioConsolidadoComSucesso() {

      Course course = new Course(); 
      course.setId(1L);

      Student s1 = new Student();
      s1.setId(1L);
      s1.setName("Aluno A");   

      Student s2 = new Student();
      s2.setId(2L);
      s2.setName("Aluno B");   

      Certificate c1 = new Certificate();
      c1.setStudent(s1);
      c1.setCourse(course);

      Certificate c2 = new Certificate();
      c2.setStudent(s2);
      c2.setCourse(course);

      when(certificateRepository.findByCourseId(1L))
          .thenReturn(Arrays.asList(c1, c2));

      ClassroomReport report = reportService.generateClassroomReport(1L);

      System.out.println("Students in Map:");
      report.performanceMap.keySet().forEach(s -> {
          System.out.println(s + " | id=" + s.getId());
      });

      System.out.println("Student s1:");
      System.out.println(s1 + " | id=" + s1.getId());

      assertEquals("OK", report.status);
      assertEquals(2, report.performanceMap.size());
      assertEquals(1, report.performanceMap.get(s1));
    }
    
    @Test
    void deveFalharComTurmaVazia() {
        Long courseId = 1L;

        when(certificateRepository.findByCourseId(courseId))
                .thenReturn(Collections.emptyList());

        assertThrows(
            IllegalArgumentException.class,
            () -> reportService.generateClassroomReport(courseId)
        );
    }
}
