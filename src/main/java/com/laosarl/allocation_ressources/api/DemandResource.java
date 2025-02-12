package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.*;
import com.laosarl.allocation_ressources.service.DemandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DemandResource {

    private final DemandService demandService;

    @PostMapping("/createrequest")
    public ResponseEntity<Void> createDemand(@RequestBody CreateDemandRequestDTO requestDTO) {
        demandService.createDemand(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<DemandDTO>> getAllDemands() {
        List<DemandDTO> demandList = demandService.getAllDemands();
        return ResponseEntity.ok(demandList);
    }

    @PatchMapping("/request/{id}")
    public ResponseEntity<Void> updateDemands(@PathVariable Long id, @RequestBody UpdateDemandDTO demandDTO) {
        demandService.updateDemand(id, demandDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/requests/delete/{id}")
    public ResponseEntity<Void> deleteDemand(@PathVariable Long id){
        demandService.deleteDemand(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/requests/updateUrgency/{id}")
    public ResponseEntity<Void> updateUrgency(@PathVariable Long id) {
        demandService.updateUrgency(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/allocate")
    public ResponseEntity<AllocatedResourceDTO> allocateResource(
            @RequestBody AllocateResourceRequestDTO request) {
        return ResponseEntity.ok(demandService.allocateResource(request));
    }
}
