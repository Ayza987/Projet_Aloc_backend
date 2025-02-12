package com.laosarl.allocation_ressources.repository;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllocatedResourceRepository extends JpaRepository<AllocatedResource, Long> {
}