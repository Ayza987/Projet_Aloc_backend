package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.CreateResourceRequestDTO;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.ResourceService;
import com.laosarl.allocation_ressources.service.mapper.ResourceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.laosarl.allocation_ressources.model.ResourceType.HARDWARE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    ResourceRepository resourceRepository;
    @Mock
    ResourceMapper resourceMapper;
    @InjectMocks
    ResourceService objectUnderTest;

    @Test
    void ShouldCreateResourceSuccessfully() {
        //Given
        CreateResourceRequestDTO requestDTO = new CreateResourceRequestDTO().name("azert").type(HARDWARE).description("").isAvailable(true).quantity(2);

        //When
        objectUnderTest.createResource(requestDTO);

        //Then
        verify(resourceRepository).save(argThat(resource -> resource.getName().equals(requestDTO.getName()) && resource.getType().equals(requestDTO.getType()) && resource.getDescription().equals(requestDTO.getDescription()) && resource.getIsAvailable().equals(requestDTO.getIsAvailable() && resource.getQuantity().equals(requestDTO.getQuantity()))));
    }

    @Test
    void getAllResources_ShouldReturnAListOfResources() {
        //Given
        List<Resource> resourceList = List.of(Resource.builder().id(1L).name("azert").type(HARDWARE).description("").isAvailable(true).quantity(2).build());
        when(resourceRepository.findAll()).thenReturn(resourceList);
        //When
        objectUnderTest.getAllResources();
        //Then
        assertThat(resourceList).hasSize(1);
    }

    @Test
    void getAllResources_ShouldReturnEmptyList_IfNoResourcesAreFound() {
        //Given
        when(resourceRepository.findAll()).thenReturn(Collections.emptyList());
        //When
        List<ResourceDTO> response = objectUnderTest.getAllResources();
        //Then
        assertNotNull(response);
        assertThat(response).isEmpty();
    }

    @Test
    void updateResource_ShouldUpdateDemandSuccessfully() {
        //Given
        Long userId = 1L;
        ResourceDTO request = new ResourceDTO().isAvailable(false);
        Resource existingResource = Resource.builder().id(1L).name("azert").type(HARDWARE).description("").isAvailable(true).quantity(2).build();

        when(resourceRepository.findById(userId)).thenReturn(Optional.of(existingResource));
        //When
        objectUnderTest.updateResource(userId, request);
        //Then
        verify(resourceMapper).updateResourceFromDto(request, existingResource);
        verify(resourceRepository).save(existingResource);
    }

    @Test
    void updateDemand_ShouldThrowException_WhenUserNotFound() {
        //Arrange
        Long userId = 1L;
        ResourceDTO request = new ResourceDTO();
        when(resourceRepository.findById(userId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.updateResource(userId, request));
    }

    @Test
    void deleteDemand_ShouldDeleteTheDemand_WhenIdIsFound() {
        //Given
        Long userId = 1L;
        Resource existingResource = Resource.builder().id(1L).name("azert").type(HARDWARE).description("").isAvailable(true).quantity(2).build();
        when(resourceRepository.findById(userId)).thenReturn(Optional.of(existingResource));

        //When
        objectUnderTest.deleteDemand(userId);

        //Then
        verify(resourceRepository).delete(existingResource);
    }

    @Test
    void changeAvailability_ShouldToggleAvailability_WhenResourceExists() {
        // Given
        Long id = 1L;
        Resource resource = new Resource();
        resource.setIsAvailable(true);

        when(resourceRepository.findById(id)).thenReturn(Optional.of(resource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        // When
        Resource result = objectUnderTest.changeAvailability(id);
        // Then
        assertFalse(result.getIsAvailable());
        verify(resourceRepository).save(resource);
    }

    @Test
    void searchResources_ShouldThrowException_WhenNoResourcesFound() {
        // Given
        String name = "azert";
        when(resourceRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());
        // When & Then
        assertThrows(RuntimeException.class, () -> objectUnderTest.searchResources(name));
    }
}
