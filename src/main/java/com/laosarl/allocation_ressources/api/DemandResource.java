package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.CreateDemandRequestDTO;
import com.laosarl.allocation_ressources.model.DemandDTO;
import com.laosarl.allocation_ressources.model.UserDTO;
import com.laosarl.allocation_ressources.service.DemandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DemandResource {

    private final DemandService demandService;

    @PostMapping("/createrequest")
    public ResponseEntity<Void> createDemand(@RequestBody CreateDemandRequestDTO requestDTO) {
        demandService.createDemand(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<DemandDTO>> getAllDemands(){
        List<DemandDTO> demandList= demandService.getAllDemands();
        return ResponseEntity.ok(demandList);
    }
}
