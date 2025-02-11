package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.CreateResourceRequestDTO;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.mapper.ResourceMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    @Value("${upload.directory}")
    private String uploadDirectory;

    @Transactional
    public void createResource(CreateResourceRequestDTO request) {
        resourceRepository.save(Resource.builder().name(request.getName()).type(request.getType()).description(request.getDescription()).isAvailable(request.getIsAvailable()).build());
    }

    public List<ResourceDTO> getAllResources() {
        List<Resource> resources = resourceRepository.findAll();

        if (resources.isEmpty()) {
            return Collections.emptyList();
        }
        return resources.stream().map(resourceMapper::toResourceDTO).toList();
    }

    public ResourceDTO getResource(Long id) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new RuntimeException("No resource found"));
        return resourceMapper.toResourceDTO(resource);
    }

    public void updateResource(Long id, ResourceDTO resourceDTO) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new RuntimeException("Demand Not found"));

        resourceMapper.updateResourceFromDto(resourceDTO, resource);

        resourceRepository.save(resource);
    }

    public void deleteDemand(Long id) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new RuntimeException("Demand Not found"));
        resourceRepository.delete(resource);
    }

    public Resource changeAvailability(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource Not found"));
        resource.setIsAvailable(!resource.getIsAvailable());
        return resourceRepository.save(resource);
    }
}
