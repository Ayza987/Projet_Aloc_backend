package com.laosarl.allocation_ressources.repository;


import com.laosarl.allocation_ressources.domain.Notification;
import com.laosarl.allocation_ressources.model.NotificationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserEmailAndIsReadFalse(String userEmail);

    int countByUserEmailAndIsReadFalse(String userEmail);
}
