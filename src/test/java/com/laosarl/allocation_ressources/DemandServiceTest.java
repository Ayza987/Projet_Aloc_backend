package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.CreateDemandRequestDTO;
import com.laosarl.allocation_ressources.model.CreateDemandResponseDTO;
import com.laosarl.allocation_ressources.repository.DemandRepository;
import com.laosarl.allocation_ressources.service.DemandService;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DemandServiceTest {
    @Mock
    DemandRepository demandRepository;
    @Mock
    DemandMapper demandMapper;
    @InjectMocks
    DemandService objectUnderTest;

    @Test
    void createDemand_WhenAllConditionsAreMet_ShouldCreateTheDemand(){
        //Given

        CreateDemandRequestDTO requestDTO = new CreateDemandRequestDTO().resourceName("stylo").quantity("1").description("stylo à bille").justification("Pour écrire").urgency("urgent").dueDate("2025/04/04");
        CreateDemandResponseDTO responseDTO = new CreateDemandResponseDTO();
        LocalDateTime expectedDateTime = LocalDateTime.now();
        LocalDate expectedDueDate = LocalDate.of(2025,04,06);

        Demand demandToSave = Demand.builder().resourceName(requestDTO.getResourceName()).quantity(requestDTO.getQuantity()).description(requestDTO.getDescription()).justification(requestDTO.getJustification()).urgency(requestDTO.getUrgency()).dueDate(expectedDueDate).dateTime(expectedDateTime).build();

        when(demandRepository.save(any(Demand.class))).thenReturn(demandToSave);
        when(demandMapper.toDemandDTO(any(Demand.class))).thenReturn(responseDTO);
        //When
        CreateDemandResponseDTO response = objectUnderTest.createDemand(requestDTO);
        //Then
        assertNotNull(response);

    }
}
