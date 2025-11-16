package com.example.appcore.service;

import com.example.appcore.enums.Status;
import com.example.appcore.enums.TypeInteration;
import com.example.appcore.model.Student;
import com.example.appcore.model.Timeline;
import com.example.appcore.repository.TimelineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeLineServiceTest {

    @Mock
    private TimelineRepository timelineRepository;

    @InjectMocks
    private TimelineService timelineService;

    @Test
    void deveRegistrarInteracaoValida() {

        Student user = new Student();
        user.setId(1L);
        user.setName("Jader");
        user.setStatus(Status.ACTIVE);

        boolean result = timelineService.registrarInteracao("comment", user);

        assertTrue(result);

        ArgumentCaptor<Timeline> captor = ArgumentCaptor.forClass(Timeline.class);
        verify(timelineRepository, times(1)).save(captor.capture());

        Timeline saved = captor.getValue();
        assertEquals(TypeInteration.COMMENT, saved.getTypeInteration());
    }

    @Test
    void naoDeveRegistrarInteracaoInvalida() {

        Student user = new Student();
        user.setId(2L);
        user.setName("César");
        user.setStatus(Status.ACTIVE);

        boolean result = timelineService.registrarInteracao("xyz", user);

        assertFalse(result);
        verify(timelineRepository, never()).save(any(Timeline.class));
    }
}