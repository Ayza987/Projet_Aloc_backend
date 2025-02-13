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

    @PostMapping("/createDemand")
    public ResponseEntity<Void> createDemand(@RequestBody CreateDemandRequestDTO requestDTO) {
        demandService.createDemand(requestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/demands")
    public ResponseEntity<List<DemandDTO>> getAllDemands() {
        List<DemandDTO> demandList = demandService.getAllDemands();
        return ResponseEntity.ok(demandList);
    }

    @GetMapping("/demands/{userEmail}")
    public ResponseEntity<List<DemandDTO>> getDemandsByEmail(@PathVariable String userEmail) {
        List<DemandDTO> demandList = demandService.getDemandsByEmail(userEmail);
        return ResponseEntity.ok(demandList);
    }

    @PatchMapping("/demand/{id}")
    public ResponseEntity<Void> updateDemands(@PathVariable Long id, @RequestBody UpdateDemandDTO demandDTO) {
        demandService.updateDemand(id, demandDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/demands/delete/{id}")
    public ResponseEntity<Void> deleteDemand(@PathVariable Long id){
        demandService.deleteDemand(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/demands/updateUrgency/{id}")
    public ResponseEntity<Void> updateUrgency(@PathVariable Long id) {
        demandService.updateUrgency(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/demand/allocate")
    public ResponseEntity<AllocatedResourceDTO> allocateResource(
            @RequestBody AllocateResourceRequestDTO request) {
        return ResponseEntity.ok(demandService.allocateResource(request));
    }

    @PostMapping("/demand/reject")
    public ResponseEntity<Void> rejectDemand(
            @RequestBody AllocateResourceRequestDTO request) {
        demandService.rejectDemand(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/demands/search")
    public ResponseEntity<List<DemandDTO>> searchResources(@RequestParam String userEmail) {
        List<DemandDTO> demandsList = demandService.searchDemands(userEmail);
        return ResponseEntity.ok(demandsList);
    }
}
