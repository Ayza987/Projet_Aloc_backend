package com.laosarl.allocation_ressources.domain;
import com.laosarl.allocation_ressources.model.DemandUrgency;
import com.laosarl.allocation_ressources.model.DemandStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.laosarl.allocation_ressources.model.DemandStatus.PENDING;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "demands")
public class Demand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "resource_name")
    private String resourceName;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "description")
    private String description;
    @Column(name = "justification")
    private String justification;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "urgency")
    @Enumerated(EnumType.STRING)
    private DemandUrgency urgency;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DemandStatus status = PENDING;
}
