package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.NotificationDTO;
import com.laosarl.allocation_ressources.service.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.laosarl.allocation_ressources.model.DemandStatus.*;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationMapperTest {

    private final NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);

    @Test
    void toNotificationDTO_Success() {
        // Given
        User user = new User();
        user.setEmail("user@example.com");

        Notification notification = Notification.builder()
                .id(1L)
                .user(user)
                .userEmail(user.getEmail())
                .status(APPROVED)
                .isRead(false)
                .build();

        // When
        NotificationDTO result = notificationMapper.toNotificationDTO(notification);

        // Then
        assertNotNull(result);
        assertEquals("user@example.com", result.getUserEmail());
        assertEquals(APPROVED, result.getStatus());

    }

}
