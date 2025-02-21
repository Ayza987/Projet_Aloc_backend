package com.laosarl.allocation_ressources.repository;

import com.laosarl.allocation_ressources.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    Optional<Resource> findByName(String resourceName);

    boolean existsByName(String name);

    List<Resource> findAllByName(String name);

    List<Resource> findByNameContainingIgnoreCase(String name);
}
