package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.NotificationDTO;
import com.laosarl.allocation_ressources.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationResource {

    private final NotificationService notificationService;

    @GetMapping("/notifications/user")
    ResponseEntity<List<NotificationDTO>> getNotifications(){
        List<NotificationDTO> notificationList = notificationService.getAllNotifications();
        return ResponseEntity.ok(notificationList);
    }

    @GetMapping("/notifications/count")
    ResponseEntity<String> countNotifications(){
        return ResponseEntity.ok(notificationService.countNotifications());
    }

}
