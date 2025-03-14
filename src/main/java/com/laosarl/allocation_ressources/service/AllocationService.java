package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.domain.AllocationStatus;
import com.laosarl.allocation_ressources.exceptions.NoResultsFoundException;
import com.laosarl.allocation_ressources.exceptions.ObjectNotFoundException;
import com.laosarl.allocation_ressources.model.AllocatedResourceDTO;
import com.laosarl.allocation_ressources.repository.AllocatedResourceRepository;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocatedResourceRepository allocatedResourceRepository;
    private final DemandMapper demandMapper;

    @Transactional
    public List<AllocatedResourceDTO> getAllocatedResources() {
        List<AllocatedResource> allocationList = allocatedResourceRepository.findAll();

        if (allocationList.isEmpty()) {
            return Collections.emptyList();
        }
        return allocationList.stream().map(demandMapper::toAllocatedResourceDTO).toList();
    }


    public List<AllocatedResourceDTO> searchAllocations(String userEmail) {
        List<AllocatedResource> allocationList = allocatedResourceRepository.findByUserEmailContainingIgnoreCase(userEmail);

        if (allocationList.isEmpty()) {
            throw new NoResultsFoundException("No results found");
        }
        return allocationList.stream().map(demandMapper::toAllocatedResourceDTO).toList();
    }

    public List<AllocatedResourceDTO> getAllocatedResourcesByUserEmail() {
        String userEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<AllocatedResource> allocationsList = allocatedResourceRepository.findAllByUserEmail(userEmail);

        if (allocationsList.isEmpty()) {
            return Collections.emptyList();
        }
        return allocationsList.stream().map(demandMapper::toAllocatedResourceDTO).toList();
    }

    public void updateStatus(UUID id) {
        AllocatedResource resource = allocatedResourceRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No allocated resource found"));

        if (resource.getStatus() == AllocationStatus.NOT_RETURNED) {
            resource.setStatus(AllocationStatus.RETURNED);
            allocatedResourceRepository.save(resource);
        }
    }

    public String countAllocatedResources() {
        long number = allocatedResourceRepository.count();

        return String.valueOf(number);
    }
}
