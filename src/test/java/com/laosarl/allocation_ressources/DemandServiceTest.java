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
import com.laosarl.allocation_ressources.service.NotificationService;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.laosarl.allocation_ressources.model.DemandStatus.*;
import static com.laosarl.allocation_ressources.model.DemandUrgency.*;
import static com.laosarl.allocation_ressources.model.ResourceType.*;
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
    NotificationService notificationService;
    @Mock
    DemandMapper demandMapper;
    @InjectMocks
    DemandService objectUnderTest;

    @BeforeEach
    void setUpSecurityContext() {

        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", null);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

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
        List<Demand> demandsList = List.of(Demand.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(1).urgency(URGENT).status(PENDING).build());
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
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UpdateDemandDTO request = new UpdateDemandDTO().status(APPROVED);
        Demand existingDemand = Demand.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(1).urgency(URGENT).status(PENDING).build();

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
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UpdateDemandDTO request = new UpdateDemandDTO();
        when(demandRepository.findById(userId)).thenReturn(Optional.empty());
        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.updateDemand(userId, request));
    }

    @Test
    void deleteDemand_ShouldDeleteTheDemand_WhenIdIsFound() {
        //Given
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Demand existingDemand = Demand.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(1).urgency(URGENT).status(PENDING).build();
        when(demandRepository.findById(userId)).thenReturn(Optional.of(existingDemand));
        //When
        objectUnderTest.deleteDemand(userId);
        //Then
        verify(demandRepository).delete(existingDemand);
    }

    @Test
    void allocateResourceSuccessfully_WhenAllConditionsAreMet() {
        //Given
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String Name = "crayon";
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("crayon").userEmail("titi@gmail.com").quantity(2);

        Demand existingDemand = Demand.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("crayon").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity(2).urgency(URGENT).status(PENDING).build();

        Resource existingResource = Resource.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).name("crayon").type(HARDWARE).description("").isAvailable(true).quantity(4).build();

        AllocatedResource allocatedResource = AllocatedResource.builder().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("crayon").userEmail("titi@gmail.com").quantity(2).demandDate(LocalDateTime.of(2025, 2, 2, 12, 0)).allocationDate(LocalDateTime.of(2025, 2, 2, 12, 0)).build();

        AllocatedResourceDTO expectedDto = new AllocatedResourceDTO().id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("crayon").quantity(2).demandDate("2025/02/02").allocationDate("2025/02/02");

        when(demandRepository.findById(userId)).thenReturn(Optional.of(existingDemand));
        when(resourceRepository.findByName(Name)).thenReturn(Optional.of(existingResource));
        when(allocatedResourceRepository.save(any())).thenReturn(allocatedResource);
        when(demandMapper.toAllocatedResourceDTO(any())).thenReturn(expectedDto);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        //When
        AllocatedResourceDTO result = objectUnderTest.allocateResource(request);

        //Then
        assertNotNull(result);
        assertEquals(request.getDemandId(), result.getId());
        assertEquals(Name, result.getResourceName());
        assertEquals(2, result.getQuantity());

        verify(demandRepository).save(existingDemand);
        verify(resourceRepository).save(existingResource);
        verify(emailService).sendEmail(eq("titi@gmail.com"), anyString(), anyString());
        verify(demandMapper).toAllocatedResourceDTO(any());

        assertEquals(DemandStatus.APPROVED, existingDemand.getStatus());
        assertEquals(2, existingResource.getQuantity());
        assertTrue(existingResource.getIsAvailable());
    }

    @Test
    void AllocateResource_ShouldThrowException_WhenResourceNotFound() {
        //Given
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("crayon").userEmail("titi@gmail.com").quantity(2);
        when(demandRepository.findById(userId)).thenReturn(Optional.empty());
        //When & Then
        assertThrows(RuntimeException.class, () -> objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_DemandNotPending() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO();
        request.setDemandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        Demand demand = new Demand();
        demand.setStatus(DemandStatus.APPROVED);

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(demand));
        //Act & Assert
        assertThrows(IllegalStateException.class, () -> objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_ResourceNotFound() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("Laptop");

        Demand demand = new Demand();
        demand.setStatus(DemandStatus.PENDING);

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(demand));
        when(resourceRepository.findByName("Laptop")).thenReturn(Optional.empty());
        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_ResourceNotAvailable() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("Laptop");
        Demand demand = new Demand();
        demand.setStatus(DemandStatus.PENDING);
        Resource resource = new Resource();
        resource.setIsAvailable(false);

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(demand));
        when(resourceRepository.findByName("Laptop")).thenReturn(Optional.of(resource));
        //Act & Assert
        assertThrows(IllegalStateException.class, () -> objectUnderTest.allocateResource(request));
    }

    @Test
    void allocateResource_QuantityExceedsStock() {
        //Arrange
        AllocateResourceRequestDTO request = new AllocateResourceRequestDTO().demandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")).resourceName("Laptop").quantity(3);

        Demand demand = new Demand();
        demand.setStatus(DemandStatus.PENDING);
        demand.setQuantity(3);

        Resource resource = new Resource();
        resource.setQuantity(3);
        resource.setIsAvailable(true);

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(demand));
        when(resourceRepository.findByName("Laptop")).thenReturn(Optional.of(resource));
        //Act & Assert
        assertThrows(IllegalStateException.class, () -> objectUnderTest.allocateResource(request));
    }


    @Test
    void rejectDemand_ShouldThrowException_WhenDemandNotFound() {
        // Given
        RejectDemandRequestDTO request = new RejectDemandRequestDTO();
        request.setDemandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.empty());
        // When & Then
        assertThrows(RuntimeException.class, () -> objectUnderTest.rejectDemand(request));
    }

    @Test
    void rejectDemand_ShouldSendEmailAndChangeStatus_WhenDemandIsPending() {
        // Given
        RejectDemandRequestDTO request = new RejectDemandRequestDTO();
        request.setDemandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        request.setResourceName("Laptop");
        request.setUserEmail("test@example.com");
        request.setRejectReason("indisponible");

        Demand demand = new Demand();
        demand.setStatus(DemandStatus.PENDING);

        when(demandRepository.findById(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(demand));
        // When
        objectUnderTest.rejectDemand(request);
        // Then
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), anyString());
        verify(notificationService).createRejectedNotification(request);
        assertEquals(DemandStatus.REJECTED, demand.getStatus());
        verify(demandRepository).save(demand);
    }
}
