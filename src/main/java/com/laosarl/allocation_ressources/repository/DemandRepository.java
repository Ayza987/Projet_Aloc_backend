package com.laosarl.allocation_ressources.repository;

import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.DemandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DemandRepository extends JpaRepository<Demand, UUID> {
    List<Demand> findAllByUserEmail(String userEmail);

    List<Demand> findByUserEmailContainingIgnoreCase(String userEmail);

    long countByStatus(DemandStatus demandStatus);

    long countByDateTimeAfterAndStatus(LocalDateTime days, DemandStatus demandStatus);
}
