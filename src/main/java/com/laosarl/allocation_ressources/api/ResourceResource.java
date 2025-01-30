package com.laosarl.allocation_ressources.api;

import com.laosarl.allocation_ressources.model.CreateResourceRequestDTO;
import com.laosarl.allocation_ressources.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResourceResource {
    private final ResourceService resourceService;

    @PostMapping("/createresource")
    public ResponseEntity<Void> createResource(@RequestBody CreateResourceRequestDTO requestDTO){
        resourceService.createResource(requestDTO);
        return ResponseEntity.ok().build();
    }
}
