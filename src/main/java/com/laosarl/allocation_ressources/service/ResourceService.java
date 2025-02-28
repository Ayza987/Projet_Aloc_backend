package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.exceptions.NoResultsFoundException;
import com.laosarl.allocation_ressources.exceptions.ObjectNotFoundException;
import com.laosarl.allocation_ressources.exceptions.ResourceAlreadyExistsException;
import com.laosarl.allocation_ressources.model.CreateResourceRequestDTO;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.model.ResourceForUserDTO;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.mapper.ResourceMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;


    @Transactional
    public void createResource(CreateResourceRequestDTO request) {
        if (resourceRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Resource already exists");
        }
        if (request.getQuantity() < 1) {
            throw new IllegalArgumentException("Resource needs quantity");
        }
        resourceRepository.save(Resource.builder().name(request.getName()).type(request.getType()).description(request.getDescription()).isAvailable(request.getIsAvailable()).quantity(request.getQuantity()).build());
    }

    public List<ResourceDTO> getAllResources() {
        List<Resource> resources = resourceRepository.findAll();

        if (resources.isEmpty()) {
            return Collections.emptyList();
        }
        return resources.stream().map(resourceMapper::toResourceDTO).toList();
    }

    public ResourceDTO getResource(UUID id) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("No resource found"));
        return resourceMapper.toResourceDTO(resource);
    }

    public void updateResource(UUID id, ResourceDTO resourceDTO) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Demand Not found"));

        resourceMapper.updateResourceFromDto(resourceDTO, resource);

        resourceRepository.save(resource);
    }

    public void deleteDemand(UUID id) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Demand Not found"));
        resourceRepository.delete(resource);
    }

    public ResourceDTO changeAvailability(UUID id) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Resource Not found"));
        resource.setIsAvailable(!resource.getIsAvailable());
        resourceRepository.save(resource);
        return (resourceMapper.toResourceDTO(resource));

    }

    public List<ResourceDTO> searchResources(String name) {
        List<Resource> resources = resourceRepository.findByNameContainingIgnoreCase(name);

        if (resources.isEmpty()) {
            throw new NoResultsFoundException("No results found");
        }
        return resources.stream().map(resourceMapper::toResourceDTO).toList();
    }

    public List<ResourceForUserDTO> getAllResourceForUser() {
        List<Resource> resources = resourceRepository.findAll();

        if (resources.isEmpty()) {
            return Collections.emptyList();
        }
        return resources.stream().map(resourceMapper::toResourceForUserDTO).toList();
    }
}

