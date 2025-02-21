package com.laosarl.allocation_ressources.domain;

import com.laosarl.allocation_ressources.model.DemandStatus;
import com.laosarl.allocation_ressources.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "demand_id", nullable = false)
    private Demand demand;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DemandStatus status;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;
}

