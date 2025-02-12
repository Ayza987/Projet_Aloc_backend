package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.*;
import com.laosarl.allocation_ressources.repository.AllocatedResourceRepository;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandService {

    @Autowired
    private EmailService emailService;
    private final DemandRepository demandRepository;
    private final ResourceRepository resourceRepository;
    private final AllocatedResourceRepository allocatedResourceRepository;
    private final DemandMapper demandMapper;

    public void createDemand(CreateDemandRequestDTO request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        LocalDate dueDateFormatted = null;
        if (request.getDueDate() != null && !request.getDueDate().isEmpty()) {
            try {
                dueDateFormatted = LocalDate.parse(request.getDueDate(), formatter);
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid Format Date");
            }
        }
        LocalDateTime currentDateTime = LocalDateTime.now();

        Demand demand = Demand.builder().resourceName(request.getResourceName()).quantity(request.getQuantity()).description(request.getDescription()).justification(request.getJustification()).urgency(request.getUrgency()).dueDate(dueDateFormatted).dateTime(currentDateTime).build();
        demandRepository.save(demand);
    }

    public List<DemandDTO> getAllDemands() {
        List<Demand> demands = demandRepository.findAll();

        if (demands.isEmpty()) {
            return Collections.emptyList();
        }
        return demands.stream().map(demandMapper::toDemandDTO).toList();
    }

    public void updateDemand(Long id, UpdateDemandDTO updateRequest) {
        Demand demand = demandRepository.findById(id).orElseThrow(() -> new RuntimeException("Demand Not found"));

        demandMapper.updateDemandFromDto(updateRequest, demand);

        demandRepository.save(demand);
    }

    public void deleteDemand(Long id) {
        Demand demand = demandRepository.findById(id).orElseThrow(() -> new RuntimeException("Demand Not found"));
        demandRepository.delete(demand);
    }

    public void updateUrgency(Long id) {
        Demand demand = demandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demand Not Found"));
        if (demand.getUrgency() == DemandUrgency.URGENT) {
            demand.setUrgency(DemandUrgency.NOT_URGENT);
        } else if (demand.getUrgency() == DemandUrgency.NOT_URGENT) {
            demand.setUrgency(DemandUrgency.URGENT);
        }
        demandRepository.save(demand);
    }

    @Transactional
    public AllocatedResourceDTO allocateResource(AllocateResourceRequestDTO request) {
        Demand demand = demandRepository.findById((request.getDemandId()))
                .orElseThrow(() -> new RuntimeException("Demand not found"));

        if (demand.getStatus() != DemandStatus.PENDING) {
            throw new IllegalStateException("Demand is not in PENDING status");
        }

        Resource resource = resourceRepository.findByName(request.getResourceName())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (!resource.getIsAvailable()) {
            throw new IllegalStateException("Resource not available");
        }

        if (request.getQuantity() > demand.getQuantity()) {
            throw new IllegalStateException("Requested quantity exceeds available stock");
        }

        AllocatedResource allocation = AllocatedResource.builder()
                .demandId(demand.getId())
                .userEmail(request.getUserEmail())
                .resourceName(request.getResourceName())
                .quantity(request.getQuantity())
                .demandDate(demand.getDateTime())
                .allocationDate(LocalDateTime.now())
                .build();
        AllocatedResource savedAllocation = allocatedResourceRepository.save(allocation);

        demand.setStatus(DemandStatus.APPROVED);
        demandRepository.save(demand);

        int remainingQuantity = (resource.getQuantity()) - request.getQuantity();
        resource.setQuantity(remainingQuantity);
        if (remainingQuantity <= 0) {
            resource.setIsAvailable(false);
        }
        resourceRepository.save(resource);

        AllocatedResourceDTO dto = new AllocatedResourceDTO();
        dto.setId(savedAllocation.getId());
        dto.setDemandId(savedAllocation.getDemandId());
        dto.setUserEmail(savedAllocation.getUserEmail());
        dto.setResourceName(savedAllocation.getResourceName());
        dto.setQuantity(savedAllocation.getQuantity());
        dto.setDemandDate(savedAllocation.getDemandDate().toString());
        dto.setAllocationDate(savedAllocation.getAllocationDate().toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = LocalDateTime.now().format(formatter);

        String subject = "Votre demande de ressource a été approuvée";
        String body = "Bonjour,\n\nVotre demande de " + request.getResourceName() + " a été approuvée.\n"
                + "Quantité allouée : " + request.getQuantity() + "\n"
                + "Date et heure d'allocation : " + formattedDate + "\n\n"
                + "Merci de récupérer votre ressource.";

        emailService.sendEmail(request.getUserEmail(), subject, body);

        return dto;


    }
}
