package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.exceptions.ObjectNotFoundException;
import com.laosarl.allocation_ressources.model.AllocateResourceRequestDTO;
import com.laosarl.allocation_ressources.model.DemandStatus;
import com.laosarl.allocation_ressources.model.NotificationDTO;
import com.laosarl.allocation_ressources.model.RejectDemandRequestDTO;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.repository.NotificationRepository;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.NotificationService;
import com.laosarl.allocation_ressources.service.mapper.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    NotificationMapper notificationMapper;
    @Mock
    private DemandRepository demandRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUpSecurityContext() {

        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", null);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createApprovedNotification_Success() {
        //Given
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO();
        request.setUserEmail("user@example.com");
        request.setDemandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        User user = new User();
        user.setEmail("user@example.com");

        Demand demand = new Demand();
        demand.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        demand.setStatus(DemandStatus.APPROVED);

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(demand));
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
        request.setDemandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        //When
        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.empty());
        //Then
        assertThrows(ObjectNotFoundException.class, () -> notificationService.createApprovedNotification(request));
    }


    @Test
    void createRejectedNotification_Success() {
        //Given
        RejectDemandRequestDTO request = new RejectDemandRequestDTO();
        request.setDemandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        request.setUserEmail("user@example.com");
        request.setRejectReason("Resource unavailable");

        User user = new User();
        user.setEmail("user@example.com");

        Demand demand = new Demand();
        demand.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(demand));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        //When
        notificationService.createRejectedNotification(request);
        //Then
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void createRejectedNotification_MissingRejectReason() {
        //Given
        RejectDemandRequestDTO request = new RejectDemandRequestDTO();
        request.setDemandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        request.setUserEmail("user@test.com");
        request.setRejectReason("");
        //When & Then
        assertThrows(IllegalArgumentException.class, () -> notificationService.createRejectedNotification(request));
    }


    @Test
    void getAllNotifications_Success() {
        //Given
        String userEmail = "user@example.com";

        Notification notification = new Notification();
        notification.setUserEmail(userEmail);

        NotificationDTO notificationDTO = new NotificationDTO();

        //when(userRepository.existsByEmail(userEmail)).thenReturn(true);
        when(notificationRepository.findByUserEmailAndIsReadFalse(userEmail)).thenReturn(List.of(notification));
        when(notificationMapper.toNotificationDTO(notification)).thenReturn(notificationDTO);
        //When
        List<NotificationDTO> result = notificationService.getAllNotifications();
        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(notificationRepository).findByUserEmailAndIsReadFalse(userEmail);
        verify(notificationMapper).toNotificationDTO(notification);

    }


    @Test
    void getAllNotifications_NoNotificationsFound() {
        //Given
        String userEmail = "user@example.com";

        when(notificationRepository.findByUserEmailAndIsReadFalse(userEmail)).thenReturn(Collections.emptyList());
        //When & Then
        assertThrows(ObjectNotFoundException.class, () -> notificationService.getAllNotifications());
    }


}
