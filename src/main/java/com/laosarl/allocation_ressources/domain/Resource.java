package com.laosarl.allocation_ressources.domain;
import com.laosarl.allocation_ressources.model.ResourceType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ResourceType type;
    private String description;
    private Boolean isAvailable;
    private Integer quantity;
}
