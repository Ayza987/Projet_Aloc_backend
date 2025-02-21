package com.laosarl.allocation_ressources.repository;


import com.laosarl.allocation_ressources.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    List<User> findByNameContainingIgnoreCase(String name);

    Optional<User> findByEmail(String email);
}

