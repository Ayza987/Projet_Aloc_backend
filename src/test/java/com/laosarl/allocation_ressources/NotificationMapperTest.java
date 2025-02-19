package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.service.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.laosarl.allocation_ressources.model.DemandStatus.*;

public class NotificationMapperTest {

    private final NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);


}
