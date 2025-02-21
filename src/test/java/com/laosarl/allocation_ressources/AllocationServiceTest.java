package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.domain.AllocationStatus;
import com.laosarl.allocation_ressources.model.AllocatedResourceDTO;
import com.laosarl.allocation_ressources.repository.AllocatedResourceRepository;
import com.laosarl.allocation_ressources.service.AllocationService;
import com.laosarl.allocation_ressources.service.mapper.AllocationMapper;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private AllocatedResourceRepository allocatedResourceRepository;

    @Mock
    private AllocationMapper allocationMapper;

    @Mock
    private DemandMapper demandMapper;

    @InjectMocks
    private AllocationService allocationService;

    @Test
    void getAllocatedResources_WhenAllocationsExist_ShouldReturnList() {
        // Given
        List<AllocatedResource> allocations = List.of(new AllocatedResource());
        when(allocatedResourceRepository.findAll()).thenReturn(allocations);
        when(demandMapper.toAllocatedResourceDTO(any())).thenReturn(new AllocatedResourceDTO());

        // When
        List<AllocatedResourceDTO> result = allocationService.getAllocatedResources();

        // Then
        assertFalse(result.isEmpty());
        verify(allocatedResourceRepository).findAll();
    }

    @Test
    void getAllocatedResources_WhenNoAllocationsExist_ShouldReturnEmptyList() {
        // Given
        when(allocatedResourceRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<AllocatedResourceDTO> result = allocationService.getAllocatedResources();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAllocations_WhenResultsFound_ShouldReturnList() {
        // Given
        String userEmail = "test@example.com";
        List<AllocatedResource> allocations = List.of(new AllocatedResource());
        when(allocatedResourceRepository.findByUserEmailContainingIgnoreCase(userEmail)).thenReturn(allocations);
        when(demandMapper.toAllocatedResourceDTO(any())).thenReturn(new AllocatedResourceDTO());

        // When
        List<AllocatedResourceDTO> result = allocationService.searchAllocations(userEmail);

        // Then
        assertFalse(result.isEmpty());
    }

    @Test
    void searchAllocations_WhenNoResultsFound_ShouldThrowException() {
        // Given
        String userEmail = "test@example.com";
        when(allocatedResourceRepository.findByUserEmailContainingIgnoreCase(userEmail)).thenReturn(Collections.emptyList());

        // When/Then
        assertThrows(RuntimeException.class, () -> allocationService.searchAllocations(userEmail));
    }

    @Test
    void getAllocatedResourcesByUserEmail_WhenAllocationsExist_ShouldReturnList() {
        // Given
        String userEmail = "test@example.com";
        List<AllocatedResource> allocations = List.of(new AllocatedResource());
        when(allocatedResourceRepository.findAllByUserEmail(userEmail)).thenReturn(allocations);
        when(demandMapper.toAllocatedResourceDTO(any())).thenReturn(new AllocatedResourceDTO());

        // When
        List<AllocatedResourceDTO> result = allocationService.getAllocatedResourcesByUserEmail(userEmail);

        // Then
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllocatedResourcesByUserEmail_WhenNoAllocationsExist_ShouldReturnEmptyList() {
        // Given
        String userEmail = "test@example.com";
        when(allocatedResourceRepository.findAllByUserEmail(userEmail)).thenReturn(Collections.emptyList());

        // When
        List<AllocatedResourceDTO> result = allocationService.getAllocatedResourcesByUserEmail(userEmail);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void updateStatus_WhenResourceExistsAndStatusNotReturned_ShouldUpdateStatus() {
        // Given
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        AllocatedResource resource = new AllocatedResource();
        resource.setStatus(AllocationStatus.NOT_RETURNED);
        when(allocatedResourceRepository.findById(id)).thenReturn(Optional.of(resource));

        // When
        allocationService.updateStatus(id);

        // Then
        assertEquals(AllocationStatus.RETURNED, resource.getStatus());
        verify(allocatedResourceRepository).save(resource);
    }

    @Test
    void updateStatus_WhenResourceDoesNotExist_ShouldThrowException() {
        // Given
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        when(allocatedResourceRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> allocationService.updateStatus(id));
    }
}

