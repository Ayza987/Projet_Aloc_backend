package com.laosarl.allocation_ressources.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.laosarl.allocation_ressources.domain.AllocationStatus.*;

@Entity
@Table(name = "allocations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocatedResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "demand_date")
    private LocalDateTime demandDate;

    @Column(name = "allocation_date")
    private LocalDateTime allocationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AllocationStatus status = NOT_RETURNED;
}
