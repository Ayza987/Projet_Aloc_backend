package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.CreateDemandRequestDTO;
import com.laosarl.allocation_ressources.model.DemandDTO;
import com.laosarl.allocation_ressources.model.UpdateDemandDTO;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.service.DemandService;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DemandServiceTest {
    @Mock
    DemandRepository demandRepository;
    @Mock
    DemandMapper demandMapper;
    @InjectMocks
    DemandService objectUnderTest;

    @Test
    void createDemand_WhenAllConditionsAreMet_ShouldCreateTheDemand() {

        //Given
        CreateDemandRequestDTO request = new CreateDemandRequestDTO().resourceName("stylo").quantity("1").description("stylo à bille").justification("Pour écrire").urgency("urgent").dueDate("2024/05/05");
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
        CreateDemandRequestDTO request = new CreateDemandRequestDTO().resourceName("stylo").quantity("1").description("stylo à bille").justification("Pour écrire").urgency("urgent").dueDate("2024-05-05");
        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.createDemand(request));
    }

    @Test
    void getAllDemands_ShouldReturnAListOfDemands() {
        //Given
        List<Demand> demandsList = List.of(Demand.builder().id(1L).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity("azertyu").urgency("urgent").status("PENDING").build());
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
        UpdateDemandDTO request = new UpdateDemandDTO().status("APPROVED");
        Demand existingDemand = Demand.builder().id(1L).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity("azertyu").urgency("urgent").status("PENDING").build();

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
    void deleteDemand_ShouldDeleteTheDemand_WhenIdIsFound(){
        //Given
        Long userId = 1L;
        Demand existingDemand = Demand.builder().id(1L).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity("azertyu").urgency("urgent").status("PENDING").build();
        when(demandRepository.findById(userId)).thenReturn(Optional.of(existingDemand));

        //When
        objectUnderTest.deleteDemand(userId);

        //Then
        verify(demandRepository).delete(existingDemand);
    }
}
