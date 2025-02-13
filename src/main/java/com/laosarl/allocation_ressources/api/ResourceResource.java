package com.laosarl.allocation_ressources.api;

import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.CreateResourceRequestDTO;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.service.ResourceService;
import com.laosarl.allocation_ressources.service.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResourceResource {
    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @PostMapping("/createresource")
    public ResponseEntity<Void> createResource(@RequestBody CreateResourceRequestDTO requestDTO) {
        resourceService.createResource(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/resources")
    public ResponseEntity<List<ResourceDTO>> getAllResources() {
        List<ResourceDTO> resourceList = resourceService.getAllResources();
        return ResponseEntity.ok(resourceList);
    }

    @GetMapping("/resources/{id}")
    public ResponseEntity<ResourceDTO> getResource(@PathVariable Long id) {
        ResourceDTO resource = resourceService.getResource(id);
        return ResponseEntity.ok(resource);
    }

    @PatchMapping("/resource/{id}")
    public ResponseEntity<Void> updateResource(@PathVariable Long id, @RequestBody ResourceDTO resource) {
        resourceService.updateResource(id, resource);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/resources/delete/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteDemand(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/resources/availability/{id}")
    public ResponseEntity<ResourceDTO> updateAvailability(@PathVariable Long id) {
        Resource updatedResource = resourceService.changeAvailability(id);
        return ResponseEntity.ok(resourceMapper.toDto(updatedResource));
    }

    @GetMapping("/resources/search")
    public ResponseEntity<List<ResourceDTO>> searchResources(@RequestParam String name) {
        List<ResourceDTO> resourceList = resourceService.searchResources(name);
        return ResponseEntity.ok(resourceList);
    }
}
