package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.exceptions.InvalidDateFormatException;
import com.laosarl.allocation_ressources.exceptions.NoResultsFoundException;
import com.laosarl.allocation_ressources.exceptions.ObjectNotFoundException;
import com.laosarl.allocation_ressources.model.*;
import com.laosarl.allocation_ressources.repository.AllocatedResourceRepository;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DemandService {

    private final EmailService emailService;
    private final DemandRepository demandRepository;
    private final ResourceRepository resourceRepository;
    private final AllocatedResourceRepository allocatedResourceRepository;
    private final NotificationService notificationService;
    private final DemandMapper demandMapper;

    public void createDemand(CreateDemandRequestDTO request) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        LocalDate dueDateFormatted = null;
        if (request.getDueDate() != null && !request.getDueDate().isEmpty()) {
            try {
                dueDateFormatted = LocalDate.parse(request.getDueDate(), formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidDateFormatException("Invalid Format Date");
            }
        }
        LocalDateTime currentDateTime = LocalDateTime.now();

        Demand demand = Demand.builder().userEmail(email).resourceName(request.getResourceName()).quantity(request.getQuantity()).description(request.getDescription()).justification(request.getJustification()).urgency(request.getUrgency()).dueDate(dueDateFormatted).dateTime(currentDateTime).build();
        demandRepository.save(demand);
    }

    public List<DemandDTO> getAllDemands() {
        List<Demand> demands = demandRepository.findAll();

        if (demands.isEmpty()) {
            return Collections.emptyList();
        }
        return demands.stream().map(demandMapper::toDemandDTO).toList();
    }

    public void updateDemand(UUID id, UpdateDemandDTO updateRequest) {
        Demand demand = demandRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Demand Not found"));

        demandMapper.updateDemandFromDto(updateRequest, demand);

        demandRepository.save(demand);
    }

    public void deleteDemand(UUID id) {
        Demand demand = demandRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Demand Not found"));
        demandRepository.delete(demand);
    }

    public void updateUrgency(UUID id) {
        Demand demand = demandRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Demand Not Found"));
        if (demand.getUrgency() == DemandUrgency.URGENT) {
            demand.setUrgency(DemandUrgency.NOT_URGENT);
        } else if (demand.getUrgency() == DemandUrgency.NOT_URGENT) {
            demand.setUrgency(DemandUrgency.URGENT);
        }
        demandRepository.save(demand);
    }

    @Transactional
    public AllocatedResourceDTO
    allocateResource(AllocateResourceRequestDTO request) {
        Demand demand = demandRepository.findById((request.getDemandId()))
                .orElseThrow(() -> new ObjectNotFoundException("Demand not found"));

        if (demand.getStatus() != DemandStatus.PENDING) {
            throw new IllegalStateException("Demand is not in PENDING status");
        }

        Resource resource = resourceRepository.findByName(request.getResourceName())
                .orElseThrow(() -> new ObjectNotFoundException("Resource not found"));

        if (!resource.getIsAvailable()) {
            throw new IllegalStateException("Resource not available");
        }

        if (request.getQuantity() >= resource.getQuantity()) {
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
        if (remainingQuantity == 1) {
            resource.setIsAvailable(false);
        }
        resourceRepository.save(resource);

        AllocatedResourceDTO dto = demandMapper.toAllocatedResourceDTO(savedAllocation);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = LocalDateTime.now().format(formatter);

        String subject = "Votre demande de ressource a été approuvée";
        String body = String.format("""
                Bonjour,
                
                Votre demande de %s a été approuvée.
                Quantité allouée : %s
                Date et heure d'allocation: %s
                
                Merci de récupérer votre ressource.
                """, request.getResourceName(), request.getQuantity(), formattedDate);

        notificationService.createApprovedNotification(request);
        emailService.sendEmail(request.getUserEmail(), subject, body);

        return dto;


    }

    public void rejectDemand(RejectDemandRequestDTO request) {
        Demand demand = demandRepository.findById((request.getDemandId()))
                .orElseThrow(() -> new ObjectNotFoundException("Demand not found"));

        if (demand.getStatus() != DemandStatus.PENDING) {
            throw new IllegalStateException("Demand is not in PENDING status");
        }

        String subject = "Votre demande de ressource a été rejetée";
        String body = String.format("""
            Bonjour,
            
            Votre demande de %s a été rejetée par l'administration avec le motif suivant :
            %s
           
            
            Cordialement.
            """, request.getResourceName(), request.getRejectReason());


        demand.setRejectReason(request.getRejectReason());
        demand.setStatus(DemandStatus.REJECTED);
        notificationService.createRejectedNotification(request);
        emailService.sendEmail(request.getUserEmail(), subject, body);

        demandRepository.save(demand);
    }


    public List<DemandDTO> searchDemands(String userEmail) {
        List<Demand> demandList = demandRepository.findByUserEmailContainingIgnoreCase(userEmail);

        if (demandList.isEmpty()) {
            throw new NoResultsFoundException("No results found");
        }
        return demandList.stream().map(demandMapper::toDemandDTO).toList();
    }

    public List<DemandDTO> getAuthenticationUserDemands() {
        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        List<Demand> demands = demandRepository.findAllByUserEmail(userEmail);

        if (demands.isEmpty()) {
            return Collections.emptyList();
        }
        return demands.stream().map(demandMapper::toDemandDTO).toList();
    }

    public String countPendingDemands() {
        long number = demandRepository.countByStatus(DemandStatus.PENDING);

        return String.valueOf(number);
    }
}
