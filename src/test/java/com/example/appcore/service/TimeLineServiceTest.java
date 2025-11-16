package com.example.appcore.service;

import com.example.appcore.enums.Status;
import com.example.appcore.enums.TypeInteration;
import com.example.appcore.model.Student;
import com.example.appcore.model.Timeline;
import com.example.appcore.repository.StudentRepository;
import com.example.appcore.repository.TimelineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Jader

@ExtendWith(MockitoExtension.class)
class TimeLineServiceTest {
    @Mock
    private TimelineRepository timelineRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private TimelineService timelineService;

    @Test
    void deveRegistrarInteracaoValida() {

        // Arrange
        Student user = new Student();
        user.setId(1L);
        user.setName("Jader");

        Timeline timelineMock = new Timeline();
        timelineMock.setId(10L);
        timelineMock.setUser(user);
        timelineMock.setTypeInteration(TypeInteration.COMMENT);

        when(timelineRepository.save(any(Timeline.class))).thenReturn(timelineMock);

        // Act
        Timeline result = timelineService.registrarInteracao(
                user.getId(),
                TypeInteration.COMMENT,
                user
        );

        // Assert
        assertNotNull(result);
        assertEquals(TypeInteration.COMMENT, result.getTypeInteration());
        assertEquals(user, result.getUser());

        ArgumentCaptor<Timeline> captor = ArgumentCaptor.forClass(Timeline.class);
        verify(timelineRepository, times(1)).save(captor.capture());

        Timeline saved = captor.getValue();
        assertEquals(TypeInteration.COMMENT, saved.getTypeInteration());
        assertEquals(user, saved.getUser());
    }

    @Test
    void naoDeveRegistrarQuandoUsuarioInvalido() {

        // Arrange
        Student user = new Student();
        user.setId(2L);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            timelineService.registrarInteracao(
                    999L,
                    TypeInteration.LIKE,
                    user
            );
        });

        verify(timelineRepository, never()).save(any());
    }
}