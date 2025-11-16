package com.example.appcore.service;

import com.example.appcore.enums.CreateStatus;
import com.example.appcore.model.Comment;
import com.example.appcore.model.Course;
import com.example.appcore.model.User;
import com.example.appcore.model.Video;
import com.example.appcore.repository.CommentRepository;
import com.example.appcore.repository.CourseRepository;
import com.example.appcore.repository.UserRepository;
import com.example.appcore.repository.VideoRepository;

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

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) {

        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome inválido");
        }

        if (course.getDescription() == null || course.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição inválida");
        }

        if (course.getPrice() == null || course.getPrice() < 0) {
            throw new IllegalArgumentException("Preço inválido");
        }

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

    // Avaliar Curso
    public boolean evaluate(Long id, Long userId, Double rating, String content) {

        if (rating == null || rating < 1 || rating > 5) {
            return false;
        }

        if (content == null || content.trim().isEmpty()) {
            return false;
        }

        if (content.contains("<script>")) {
            return false;
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Comment comment = new Comment();
        comment.setRating(rating);
        comment.setContent(content);
        comment.setCourse(course);
        comment.setAuthor(user);
        comment.setAuthorName(user.getName());

        course.getComments().add(comment);

        courseRepository.save(course);
        return true;

    }
    
     // Editar Avaliação
    public boolean editEvaluate(Long id, Long userId, Double rating, String content) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Usuário não autorizado a editar este comentário");
        }

        if (rating == null || rating < 1 || rating > 5) return false;
        if (content == null || content.trim().isEmpty()) return false;
        if (content.contains("<script>")) return false;

        comment.setRating(rating);
        comment.setContent(content);

        commentRepository.save(comment);

        return true;
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
