package com.laosarl.allocation_ressources.service.mapper;


import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.model.NotificationDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id")
    @Mapping(target = "status")
    @Mapping(target = "userEmail")
    @Mapping(target = "rejectReason")
    NotificationDTO toNotificationDTO (Notification notification);
}
