package com.laosarl.allocation_ressources.repository;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.model.AllocatedResourceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AllocatedResourceRepository extends JpaRepository<AllocatedResource, UUID> {
    List<AllocatedResource> findByUserEmailContainingIgnoreCase(String userEmail);

    List<AllocatedResource> findAllByUserEmail(String userEmail);

}