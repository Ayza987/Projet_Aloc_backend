package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.CreateResourceRequestDTO;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.ResourceService;
import com.laosarl.allocation_ressources.service.mapper.ResourceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.laosarl.allocation_ressources.model.ResourceType.HARDWARE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
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
@Captor
    ArgumentCaptor<Resource> resourceArgumentCaptor;

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
        List<Resource> resourceList = List.of(Resource.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).name("azert").type(HARDWARE).description("").isAvailable(true).quantity(2).build());
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
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        ResourceDTO request = new ResourceDTO().isAvailable(false);
        Resource existingResource = Resource.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).name("azert").type(HARDWARE).description("").isAvailable(true).quantity(2).build();

        given(resourceRepository.findById(userId)).willReturn(Optional.of(existingResource));

        //When
        objectUnderTest.updateResource(userId, request);
        //Then
        verify(resourceMapper).updateResourceFromDto(request, existingResource);
        verify(resourceRepository).save(existingResource);
    }

    @Test
    void updateDemand_ShouldThrowException_WhenUserNotFound() {
        //Arrange
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        ResourceDTO request = new ResourceDTO();
        when(resourceRepository.findById(userId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.updateResource(userId, request));
    }

    @Test
    void deleteDemand_ShouldDeleteTheDemand_WhenIdIsFound() {
        //Given
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Resource existingResource = Resource.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).name("azert").type(HARDWARE).description("").isAvailable(true).quantity(2).build();
        when(resourceRepository.findById(userId)).thenReturn(Optional.of(existingResource));

        //When
        objectUnderTest.deleteDemand(userId);

        //Then
        verify(resourceRepository).delete(existingResource);
    }

    @Test
    void changeAvailability_ShouldToggleAvailability_WhenResourceExists() {
        // Given
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Resource resource = new Resource();
        resource.setIsAvailable(true);

        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setIsAvailable(false);

        when(resourceRepository.findById(id)).thenReturn(Optional.of(resource));
        when(resourceMapper.toResourceDTO(resource)).thenReturn(resourceDTO);
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        // When
        ResourceDTO result = objectUnderTest.changeAvailability(id);
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
