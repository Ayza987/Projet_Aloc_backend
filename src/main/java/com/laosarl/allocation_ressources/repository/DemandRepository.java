package com.laosarl.allocation_ressources.repository;

import com.laosarl.allocation_ressources.domain.Demand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {
}
