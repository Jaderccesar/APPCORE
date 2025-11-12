package com.example.appcore.appcore.service;

import com.example.appcore.appcore.enums.CreateStatus;
import com.example.appcore.appcore.model.Course;
import com.example.appcore.appcore.model.Video;
import com.example.appcore.appcore.repository.CourseRepository;
import com.example.appcore.appcore.repository.VideoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VideoRepository videoRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) { 
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setStatus(CreateStatus.DRAFT);
        return courseRepository.save(course);
    }

    public Course update(Long id, Course course) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + id));

        existing.setTitle(course.getTitle());
        existing.setDescription(course.getDescription());
        existing.setPrice(course.getPrice());
        existing.setImageUrl(course.getImageUrl());
        existing.setRating(course.getRating());
        existing.setLevel(course.getLevel());
        existing.setWorkload(course.getWorkload());
        existing.setCertificateEnabled(course.isCertificateEnabled());
        existing.setStatus(course.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        return courseRepository.save(existing);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    
    // ----------  VIDEOS  ----------

    public List<Video> findVideosByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + courseId));

        return videoRepository.findByCourse(course);
    }
     
    public Video addVideoToCourse(Long courseId, Video video) {
         Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + courseId));

        video.setCourse(course);
        course.getVideos().add(video);

        Video savedVideo = videoRepository.save(video);  
        return savedVideo;
    }
    
    public Video updateVideo(Long courseId, Long videoId, Video videoData) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + courseId));

        Video video = course.getVideos().stream()
                .filter(v -> v.getId().equals(videoId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Vídeo não encontrado no curso"));

        video.setTitle(videoData.getTitle());
        video.setDescription(videoData.getDescription());
        video.setVideoUrl(videoData.getVideoUrl());
        video.setOrderNumber(videoData.getOrderNumber());
        video.setUpdatedAt(LocalDateTime.now());

        courseRepository.save(course);
        return video;
    }

    public void deleteVideo(Long courseId, Long videoId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado com id: " + courseId));

        boolean removed = course.getVideos().removeIf(v -> v.getId().equals(videoId));

        if (!removed) {
            throw new EntityNotFoundException("Vídeo não encontrado no curso");
        }

        courseRepository.save(course);
    } 
     
}
