package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.AllocatedResourceDTO;
import com.laosarl.allocation_ressources.repository.AllocatedResourceRepository;
import com.laosarl.allocation_ressources.service.AllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AllocationResource {

    private final AllocationService allocationService;

    @GetMapping("/allocatedResources")
    public ResponseEntity<List<AllocatedResourceDTO>> getAllocatedResources(){
        return ResponseEntity.ok(allocationService.getAllocatedResources());
    }

    @GetMapping("/allocatedResource/{userEmail}")
    public ResponseEntity<List<AllocatedResourceDTO>> getAllocatedResourceByUserEmail(@PathVariable String userEmail){
        List<AllocatedResourceDTO> allocationList = allocationService.getAllocatedResourcesByUserEmail(userEmail);
        return ResponseEntity.ok(allocationList);
    }

    @GetMapping("/allocatedResources/search")
    public ResponseEntity<List<AllocatedResourceDTO>> searchAllocatedResources(@RequestParam String userEmail){
        List<AllocatedResourceDTO> allocationList = allocationService.searchAllocations(userEmail);
        return ResponseEntity.ok(allocationList);
    }

    @PatchMapping("/allocatedResource/updateStatus/{id}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id){
        allocationService.updateStatus(id);
        return ResponseEntity.ok().build();
    }


}
