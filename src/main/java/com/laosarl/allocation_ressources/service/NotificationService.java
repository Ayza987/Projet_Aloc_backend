package com.laosarl.allocation_ressources.service;


import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.exceptions.ObjectNotFoundException;
import com.laosarl.allocation_ressources.model.AllocateResourceRequestDTO;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.repository.NotificationRepository;
import com.laosarl.allocation_ressources.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final DemandRepository demandRepository;

    @Transactional
    public void createApprovedNotification(AllocateResourceRequestDTO request) {
        Demand demand = demandRepository.findById(request.getDemandId())
                .orElseThrow(() -> new ObjectNotFoundException("Demand not found"));

        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .userEmail(user.getEmail())
                .demand(demand)
                .status(demand.getStatus())
                .build();

        notificationRepository.save(notification);
    }


}
