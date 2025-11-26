package com.example.appcore.controller;

import com.example.appcore.model.Course;
import com.example.appcore.model.Video;
import com.example.appcore.repository.StudentRepository;
import com.example.appcore.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/list")
    public List<Map<String, Object>> listCourses(@RequestParam(required = false) Long userId) {

        List<Course> allCourses = courseService.findAll();

        Set<Long> purchasedCourseIds = Set.of();

        if (userId != null) {
            try {
                purchasedCourseIds = courseService.getPurchasedCourseIds(userId);
            } catch (EntityNotFoundException e) {
                System.err.println("Aluno não encontrado para verificar status de compra: " + userId);
            }
        }

        Set<Long> finalPurchasedCourseIds = purchasedCourseIds;
        return allCourses.stream()
                .map(course -> {
                    Map<String, Object> courseMap = objectMapper.convertValue(course, Map.class);

                    boolean isPurchased = finalPurchasedCourseIds.contains(course.getId());
                    courseMap.put("isPurchased", isPurchased);

                    return courseMap;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCourseDetails(@PathVariable Long id, @RequestParam(required = false) Long userId) {

        Optional<Course> course = courseService.findById(id);

        Map<String, Object> courseDetails = objectMapper.convertValue(course, Map.class);

        boolean isPurchased = false;
        if (userId != null) {
            isPurchased = courseService.checkIfStudentPurchasedCourse(userId, id);
        }

        courseDetails.put("isPurchased", isPurchased);
        return courseDetails;
    }

    @PostMapping("/save")
    public Course save(@RequestBody Course course) {
        return courseService.save(course);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody Course course) {
        try {
            return ResponseEntity.ok(courseService.update(id, course));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> listarCursosDoProfessor(@PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.listarPorProfessor(teacherId)); 
    }


    @GetMapping("/{courseId}/videos")
    public List<Video> listVideosByCourse(@PathVariable Long courseId) {
        return courseService.findVideosByCourse(courseId);
    }
    
    @PostMapping("/{courseId}/videos")
    public Video addVideo(@PathVariable Long courseId, @RequestBody Video video) {
        return courseService.addVideoToCourse(courseId, video);
    }

    @PutMapping("/{courseId}/videos/{videoId}")
    public Video updateVideo(@PathVariable Long courseId, @PathVariable Long videoId, @RequestBody Video video) {
        return courseService.updateVideo(courseId, videoId, video);
    }

    @DeleteMapping("/{courseId}/videos/{videoId}")
    public void deleteVideo(@PathVariable Long courseId, @PathVariable Long videoId) {
        courseService.deleteVideo(courseId, videoId);
    }
}
