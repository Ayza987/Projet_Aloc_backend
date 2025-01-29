package com.laosarl.allocation_ressources.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Column(name = "userName")
    private String userName;
    @Column(name = "user_email")
    private String userEmail;
    @Column(name = "description")
    private String description;
    @Column(name = "justification")
    private String justification;
    @Column(name = "quantity")
    private String quantity;
    @Column(name = "urgency")
    private String urgency;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "status")
    @Builder.Default
    private String status = "PENDING";
}
