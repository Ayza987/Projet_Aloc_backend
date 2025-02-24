package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.NotificationDTO;
import com.laosarl.allocation_ressources.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PostMapping("/notifications/markRead/{id}")
    ResponseEntity<Void> markReadNotification(@PathVariable UUID id){
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

}
