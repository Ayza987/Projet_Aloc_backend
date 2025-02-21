package com.laosarl.allocation_ressources.service;


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
import com.laosarl.allocation_ressources.service.mapper.NotificationMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final DemandRepository demandRepository;

    @Transactional
    public void createApprovedNotification(AllocateResourceRequestDTO request) {
        Demand demand = demandRepository.findById(request.getDemandId()).orElseThrow(() -> new ObjectNotFoundException("Demand not found"));

        User user = userRepository.findByEmail(request.getUserEmail()).orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Notification notification = Notification.builder().user(user).userEmail(user.getEmail()).demand(demand).status(demand.getStatus()).build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void createRejectedNotification(RejectDemandRequestDTO request) {

        if (request.getRejectReason() == null || request.getRejectReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reject reason is required for rejected demands");
        }
        Demand demand = demandRepository.findById(request.getDemandId()).orElseThrow(() -> new ObjectNotFoundException("Demand not found"));
        User user = userRepository.findByEmail(request.getUserEmail()).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Notification notification = Notification.builder().user(user).userEmail(user.getEmail()).demand(demand).status(DemandStatus.REJECTED).rejectReason(request.getRejectReason()).build();
        notificationRepository.save(notification);
    }


    public List<NotificationDTO> getAllNotifications() {
        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<Notification> list = notificationRepository.findByUserEmailAndIsReadFalse(userEmail);
        if (list.isEmpty()) {
            throw new ObjectNotFoundException("No notifications found");
        }

        return list.stream().map(notificationMapper::toNotificationDTO).toList();
    }
}
