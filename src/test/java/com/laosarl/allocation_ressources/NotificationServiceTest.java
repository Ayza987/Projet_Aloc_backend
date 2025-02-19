package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.exceptions.ObjectNotFoundException;
import com.laosarl.allocation_ressources.model.AllocateResourceRequestDTO;
import com.laosarl.allocation_ressources.model.DemandStatus;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.repository.NotificationRepository;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.NotificationService;
import com.laosarl.allocation_ressources.service.mapper.NotificationMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private DemandRepository demandRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationService notificationService;

    public NotificationServiceTest() {
    }

    @Test
    void createApprovedNotification_Success() {
        //Given
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO();
        request.setUserEmail("user@example.com");
        request.setDemandId(1L);

        User user = new User();
        user.setEmail("user@example.com");

        Demand demand = new Demand();
        demand.setId(1L);
        demand.setStatus(DemandStatus.APPROVED);

        when(demandRepository.findById(1L)).thenReturn(Optional.of(demand));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        //When
        notificationService.createApprovedNotification(request);
        //Then
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createApprovedNotification_DemandNotFound() {
        //Given
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO();
        request.setUserEmail("example@ex.com");
        request.setDemandId(1L);
        //When
        when(demandRepository.findById(1L)).thenReturn(Optional.empty());
        //Then
        assertThrows(ObjectNotFoundException.class, () -> notificationService.createApprovedNotification(request));
    }

    @Test
    void createApprovedNotification_UserNotFound() {
        //Given
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO();
        request.setUserEmail("user@example.com");
        request.setDemandId(1L);

        Demand demand = new Demand();
        demand.setId(1L);

        when(demandRepository.findById(1L)).thenReturn(Optional.of(demand));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        //When & Then
        assertThrows(ObjectNotFoundException.class, () -> notificationService.createApprovedNotification(request));
    }

}
