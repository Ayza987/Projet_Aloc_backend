package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.CreateResourceRequestDTO;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.mapper.ResourceMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public void createResource(CreateResourceRequestDTO request) {
        resourceRepository.save(Resource.builder().name(request.getName()).type(request.getType()).description(request.getDescription()).isAvailable(request.getIsAvailable()).build());
    }
}
