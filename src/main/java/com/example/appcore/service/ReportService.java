package com.example.appcore.service;

import com.example.appcore.model.Certificate;
import com.example.appcore.model.Student;
import com.example.appcore.model.Course;
import com.example.appcore.repository.CertificateRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
 
    private final CertificateRepository certificateRepository;

    public ReportService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public UserPerformanceReport generateUserReport(Long userId, Integer periodDays) {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        LocalDate limitDate = (periodDays == null)
                ? LocalDate.MIN
                : LocalDate.now().minusDays(periodDays);

        List<Certificate> certificates = certificateRepository.findAll()
                .stream()
                .filter(c -> c.getStudent() != null && Objects.equals(c.getStudent().getId(), userId))
                .filter(c -> c.getIssueDate() != null && !c.getIssueDate().isBefore(limitDate))
                .collect(Collectors.toList());

        if (certificates.isEmpty()) {
            return new UserPerformanceReport(userId, Collections.emptyList(), "Sem dados de desempenho");
        }

        return new UserPerformanceReport(userId, certificates, "OK");
    }


    public ClassroomReport generateClassroomReport(Long courseId) {

        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Invalid course ID");
        }

        List<Certificate> certificates = certificateRepository.findByCourseId(courseId);

        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("Turma sem alunos para gerar relatório");
        }

        Map<Student, Long> performance = certificates.stream()
                .collect(Collectors.groupingBy(
                        Certificate::getStudent,
                        Collectors.counting()
                ));

        return new ClassroomReport(performance, "OK");
    }

    public static class UserPerformanceReport {
        public Long userId;
        public List<Certificate> certificates; 
        public String status;

        public UserPerformanceReport(Long userId, List<Certificate> certificates, String status) {
            this.userId = userId;
            this.certificates = certificates;
            this.status = status;
        }
    }

    public static class ClassroomReport {
        public Map<Student, Long> performanceMap;
        public String status;

        public ClassroomReport(Map<Student, Long> performanceMap, String status) {
            this.performanceMap = performanceMap;
            this.status = status;
        }
    }
}
