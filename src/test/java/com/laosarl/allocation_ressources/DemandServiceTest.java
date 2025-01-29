package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.CreateDemandRequestDTO;
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
import java.time.format.DateTimeFormatter;

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
        LocalDateTime formattedDateTime = LocalDateTime.now();

        //When
        objectUnderTest.createDemand(request);
        //Then
        verify(demandRepository).save(argThat(demand -> demand.getResourceName().equals(request.getResourceName()) && demand.getDescription().equals(request.getDescription()) && demand.getJustification().equals(request.getJustification()) && demand.getDueDate().equals(formattedDueDate)));
    }
}
