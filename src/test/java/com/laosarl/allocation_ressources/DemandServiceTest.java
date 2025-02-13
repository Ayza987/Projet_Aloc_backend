package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.*;
import com.laosarl.allocation_ressources.repository.AllocatedResourceRepository;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.repository.ResourceRepository;
import com.laosarl.allocation_ressources.service.DemandService;
import com.laosarl.allocation_ressources.service.EmailService;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.laosarl.allocation_ressources.model.DemandStatus.APPROVED;
import static com.laosarl.allocation_ressources.model.DemandStatus.PENDING;
import static com.laosarl.allocation_ressources.model.DemandUrgency.URGENT;
import static com.laosarl.allocation_ressources.model.ResourceType.HARDWARE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DemandServiceTest {
    @Mock
    DemandRepository demandRepository;
    @Mock
    ResourceRepository resourceRepository;
    @Mock
    AllocatedResourceRepository allocatedResourceRepository;
    @Mock
    EmailService emailService;
    @Mock
    DemandMapper demandMapper;
    @InjectMocks
    DemandService objectUnderTest;

    @Test
    void createDemand_WhenAllConditionsAreMet_ShouldCreateTheDemand() {

        //Given
        CreateDemandRequestDTO request = new CreateDemandRequestDTO().resourceName("stylo").quantity(1).description("stylo à bille").justification("Pour écrire").urgency(URGENT).dueDate("2024/05/05");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate formattedDueDate = LocalDate.parse(request.getDueDate(), formatter);
        //When
        objectUnderTest.createDemand(request);
        //Then
        verify(demandRepository).save(argThat(demand -> demand.getResourceName().equals(request.getResourceName()) && demand.getDescription().equals(request.getDescription()) && demand.getJustification().equals(request.getJustification()) && demand.getDueDate().equals(formattedDueDate)));
    }

    @Test
    void createDemand_ShouldThrowException_WhenFormatIsInvalid() {
        //Arrange
        CreateDemandRequestDTO request = new CreateDemandRequestDTO().resourceName("stylo").quantity(1).description("stylo à bille").justification("Pour écrire").urgency(URGENT).dueDate("2024-05-05");
        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.createDemand(request));
    }

    @Test
    void getAllDemands_ShouldReturnAListOfDemands() {
        //Given
        List<Demand> demandsList = List.of(Demand.builder().id(1L).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(1).urgency(URGENT).status(PENDING).build());
        when(demandRepository.findAll()).thenReturn(demandsList);
        //When
        objectUnderTest.getAllDemands();
        //Then
        assertThat(demandsList).hasSize(1);
    }

    @Test
    void getAllDemands_ShouldReturnEmptyList_IfNoDemandsAreFound() {
        //Given
        when(demandRepository.findAll()).thenReturn(Collections.emptyList());
        //When
        List<DemandDTO> response = objectUnderTest.getAllDemands();
        //Then
        assertNotNull(response);
        assertThat(response).isEmpty();
    }

    @Test
    void updateDemand_ShouldUpdateDemandSuccessfully() {
        //Given
        Long userId = 1L;
        UpdateDemandDTO request = new UpdateDemandDTO().status(APPROVED);
        Demand existingDemand = Demand.builder().id(1L).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(1).urgency(URGENT).status(PENDING).build();

        when(demandRepository.findById(userId)).thenReturn(Optional.of(existingDemand));
        //When
        objectUnderTest.updateDemand(userId, request);
        //Then
        verify(demandMapper).updateDemandFromDto(request, existingDemand);
        verify(demandRepository).save(existingDemand);
    }

    @Test
    void updateDemand_ShouldThrowException_WhenUserNotFound() {
        //Arrange
        Long userId = 1L;
        UpdateDemandDTO request = new UpdateDemandDTO();
        when(demandRepository.findById(userId)).thenReturn(Optional.empty());
        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.updateDemand(userId, request));
    }

    @Test
    void deleteDemand_ShouldDeleteTheDemand_WhenIdIsFound() {
        //Given
        Long userId = 1L;
        Demand existingDemand = Demand.builder().id(1L).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(1).urgency(URGENT).status(PENDING).build();
        when(demandRepository.findById(userId)).thenReturn(Optional.of(existingDemand));
        //When
        objectUnderTest.deleteDemand(userId);
        //Then
        verify(demandRepository).delete(existingDemand);
    }

    @Test
    void allocateResourceSuccessfully_WhenAllConditionsAreMet() {
        //Given
        Long Id = 1L;
        String Name = "crayon";
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(1L).resourceName("crayon").userEmail("titi@gmail.com").quantity(2);
        Demand existingDemand = Demand.builder().id(1L).resourceName("crayon").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(2).urgency(URGENT).status(PENDING).build();
        Resource existingResource = Resource.builder().id(1L).name("crayon").type(HARDWARE).description("").isAvailable(true).quantity(3).build();
        AllocatedResource allocatedResource = AllocatedResource.builder().id(1L).demandId(1L).resourceName("crayon").userEmail("titi@gmail.com").quantity(2).demandDate(LocalDateTime.of(2025, 2, 2, 12, 0)).allocationDate(LocalDateTime.of(2025, 2, 2, 12, 0)).build();

        when(demandRepository.findById(Id)).thenReturn(Optional.of(existingDemand));
        when(resourceRepository.findByName(Name)).thenReturn(Optional.of(existingResource));
        when(allocatedResourceRepository.save(any())).thenReturn(allocatedResource);

        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        //When
        AllocatedResourceDTO result = objectUnderTest.allocateResource(request);

        //Then
        assertNotNull(result);
        assertEquals(Id, result.getId());
        assertEquals(Name, result.getResourceName());
        assertEquals(2, result.getQuantity());

        verify(demandRepository).save(existingDemand);
        verify(resourceRepository).save(existingResource);
        verify(emailService).sendEmail(eq("titi@gmail.com"), anyString(), anyString());

        assertEquals(DemandStatus.APPROVED, existingDemand.getStatus());
        assertEquals(1, existingResource.getQuantity());
    }

    @Test
    void AllocateResource_ShouldThrowException_WhenResourceNotFound(){
        //Given
        Long Id = 1L;
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(1L).resourceName("crayon").userEmail("titi@gmail.com").quantity(2);
        when(demandRepository.findById(Id)).thenReturn(Optional.empty());
        //When & Then
        assertThrows(RuntimeException.class, ()->objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_DemandNotPending() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO();
        request.setDemandId(1L);

        Demand demand = new Demand();
        demand.setStatus(DemandStatus.APPROVED);

        when(demandRepository.findById(1L)).thenReturn(Optional.of(demand));
        //Act & Assert
        assertThrows(IllegalStateException.class, () -> objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_ResourceNotFound() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(1L).resourceName("Laptop");

        Demand demand = new Demand();
        demand.setStatus(DemandStatus.PENDING);

        when(demandRepository.findById(1L)).thenReturn(Optional.of(demand));
        when(resourceRepository.findByName("Laptop")).thenReturn(Optional.empty());
        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_ResourceNotAvailable() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(1L).resourceName("Laptop");
        Demand demand = new Demand();
        demand.setStatus(DemandStatus.PENDING);
        Resource resource = new Resource();
        resource.setIsAvailable(false);

        when(demandRepository.findById(1L)).thenReturn(Optional.of(demand));
        when(resourceRepository.findByName("Laptop")).thenReturn(Optional.of(resource));
        //Act & Assert
        assertThrows(IllegalStateException.class, () -> objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_QuantityExceedsStock() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(1L).resourceName("Laptop").quantity(3);

        Demand demand = new Demand();
        demand.setStatus(DemandStatus.PENDING);
        demand.setQuantity(2);

        Resource resource = new Resource();
        resource.setIsAvailable(true);

        when(demandRepository.findById(1L)).thenReturn(Optional.of(demand));
        when(resourceRepository.findByName("Laptop")).thenReturn(Optional.of(resource));
        //Act & Assert
        assertThrows(IllegalStateException.class, () -> objectUnderTest.allocateResource(request));
    }
}
