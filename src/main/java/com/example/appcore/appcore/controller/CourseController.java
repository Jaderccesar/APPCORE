package com.example.appcore.appcore.controller;

import com.example.appcore.appcore.model.Course;
import com.example.appcore.appcore.model.Video;
import com.example.appcore.appcore.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list") 
    public List<Course> list() {
        return courseService.findAll(); 
    } 

    @GetMapping("/{id}")
    public ResponseEntity<Course> listById(@PathVariable Long id) {
        return courseService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    // ----------  VIDEOS  ----------

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
