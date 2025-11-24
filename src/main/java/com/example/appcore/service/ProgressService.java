package com.example.appcore.service;

import com.example.appcore.model.*;
import com.example.appcore.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private ChallengeResultRepository challengeResultRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    // INSCRIÇÃO EM CURSO
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {

        Optional<Enrollment> existing =
                enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (existing.isPresent()) return existing.get();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setCompleted(false);
        enrollment.setEnrolledAt(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    // COMPLETAR VIDEO
    @Transactional
    public Map<String, Object> completeVideo(Long studentId, Long courseId, Long videoId) {

        Enrollment enrollment =
                enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                        .orElseThrow(() -> new RuntimeException("Aluno não inscrito no curso"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Vídeo não encontrado"));

        boolean already =
                lessonProgressRepository.existsByEnrollmentIdAndVideoIdAndCompletedTrue(
                        enrollment.getId(), videoId);

        if (!already) {

            LessonProgress lp = new LessonProgress();
            lp.setEnrollment(enrollment);
            lp.setVideo(video);
            lp.setCompleted(true);
            lp.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(lp);

            // pontuação
            Student student = enrollment.getStudent();
            student.setTotalScore(student.getTotalScore() + 5);
            studentRepository.save(student);
        }

        long completed = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(enrollment.getId());
        long total = enrollment.getCourse().getVideos().size();
        int pct = total == 0 ? 0 : (int) ((completed * 100) / total);

        // finalizar curso
        if (completed == total && total > 0 && !enrollment.isCompleted()) {
            enrollment.setCompleted(true);
            enrollmentRepository.save(enrollment);

            Student student = enrollment.getStudent();
            student.setTotalScore(student.getTotalScore() + 50);
            studentRepository.save(student);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("completedVideos", completed);
        response.put("totalVideos", total);
        response.put("percentage", pct);

        return response;
    }

    // COMPLETAR DESAFIO
    @Transactional
    public ChallengeResult completeChallenge(Long studentId, Long challengeId, int score) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Desafio não encontrado"));

        ChallengeResult cr = new ChallengeResult();
        cr.setStudent(student);
        cr.setChallenge(challenge);
        cr.setScore(score);
        cr.setCompleted(true);
        cr.setCompletedAt(LocalDateTime.now());

        challengeResultRepository.save(cr);

        student.setTotalScore(student.getTotalScore() + score);
        studentRepository.save(student);

        return cr;
    }

    // PROGRESSO GERAL DO ALUNO
    public Map<String, Object> getStudentProgress(Long studentId) {

        Map<String, Object> out = new HashMap<>();

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        List<Map<String, Object>> coursesInProgress = enrollments.stream().map(en -> {

            long completed = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(en.getId());
            long total = en.getCourse().getVideos().size();
            int pct = total == 0 ? 0 : (int) ((completed * 100) / total);

            Map<String, Object> map = new HashMap<>();
            map.put("courseId", en.getCourse().getId());
            map.put("title", en.getCourse().getTitle());
            map.put("completedLessons", completed);
            map.put("totalLessons", total);
            map.put("percentage", pct);
            map.put("completed", en.isCompleted());
            return map;

        }).collect(Collectors.toList());

        long coursesEnrolled = enrollments.size();
        long coursesCompleted = enrollments.stream().filter(Enrollment::isCompleted).count();
        long challengesSolved = challengeResultRepository.countByStudentIdAndCompletedTrue(studentId);

        Student student = studentRepository.findById(studentId).orElse(null);
        int points = student != null ? student.getTotalScore() : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("coursesEnrolled", coursesEnrolled);
        stats.put("coursesCompleted", coursesCompleted);
        stats.put("challengesSolved", challengesSolved);
        stats.put("points", points);

        out.put("coursesInProgress", coursesInProgress);
        out.put("stats", stats);

        return out;
    }
}
