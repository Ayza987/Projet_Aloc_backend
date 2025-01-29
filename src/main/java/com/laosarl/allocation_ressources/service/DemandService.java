package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.CreateDemandRequestDTO;
import com.laosarl.allocation_ressources.model.DemandDTO;
import com.laosarl.allocation_ressources.model.UpdateDemandDTO;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import lombok.RequiredArgsConstructor;
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

    private final DemandRepository demandRepository;
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
        Demand demand = demandRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));

        demandMapper.updateDemandFromDto(updateRequest, demand);

        demandRepository.save(demand);
    }
}
