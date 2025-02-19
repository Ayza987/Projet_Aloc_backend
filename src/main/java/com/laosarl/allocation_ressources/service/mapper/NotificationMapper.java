package com.laosarl.allocation_ressources.service.mapper;


import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.model.NotificationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDTO toNotificationDTO (Notification notification);
}
