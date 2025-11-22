package com.example.appcore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.appcore.model.Certificate;
import com.example.appcore.model.Course;
import com.example.appcore.model.Student;
import com.example.appcore.repository.CertificateRepository;
import com.example.appcore.repository.CourseRepository;
import com.example.appcore.repository.StudentRepository;
import com.example.appcore.service.ReportService.ClassroomReport;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class ReportServiceIntegrationTest {

  @Autowired
  private ReportService reportService;

  @Autowired
  private CertificateRepository certificateRepository;

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Test
  void GerarRelatorioConsolidadoComSucesso() {

    // ---- Criar curso real ----
    Course course = new Course();
    course.setTitle("Curso Teste");
    course = courseRepository.save(course);
    Long courseId = course.getId();

    Student s1 = new Student();
    s1.setName("Aluno A");
    s1 = studentRepository.save(s1);

    Student s2 = new Student();
    s2.setName("Aluno B");
    s2 = studentRepository.save(s2);

    // ---- Criar certificados reais ----
    Certificate c1 = new Certificate();
    c1.setCourse(course);
    c1.setStudent(s1);
    certificateRepository.save(c1);

    Certificate c2 = new Certificate();
    c2.setCourse(course);
    c2.setStudent(s2);
    certificateRepository.save(c2);

    ClassroomReport report = reportService.generateClassroomReport(courseId);

    // Validações 
    assertEquals("OK", report.status);
    assertEquals(2, report.performanceMap.size());

    assertEquals(1, report.performanceMap.get(s1));
  }

  @Test
  void deveFalharComTurmaVazia() {

    Course course = new Course();
    course.setTitle("Curso Vazio");
    course = courseRepository.save(course);
    Long id = course.getId();

    assertThrows(
        IllegalArgumentException.class,
        () -> reportService.generateClassroomReport(id));
  }
}