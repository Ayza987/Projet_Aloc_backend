package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.CreateDemandRequestDTO;
import com.laosarl.allocation_ressources.model.CreateDemandResponseDTO;
import com.laosarl.allocation_ressources.service.DemandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemandResource {

    private final DemandService demandService;

    @PostMapping("/request")
    public ResponseEntity<CreateDemandResponseDTO> createDemand(@RequestBody CreateDemandRequestDTO requestDTO){
        CreateDemandResponseDTO responseDTO = demandService.createDemand(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
