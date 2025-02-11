package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.DemandDTO;
import com.laosarl.allocation_ressources.service.mapper.DemandMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.laosarl.allocation_ressources.model.DemandUrgency.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DemandMapperTest {

    private final DemandMapper demandMapper = Mappers.getMapper(DemandMapper.class);

    @Test
    void toDemandDTO_ShouldMapDemandCorrectly() {
        //Given
        Demand demandToMap = Demand.builder().id(1L).resourceName("azert").userName("azer").userEmail("azerty").description("sdfg").justification("azertyu").quantity("azertyu").urgency(URGENT).status("PENDING").build();
        //When
        DemandDTO dto = demandMapper.toDemandDTO(demandToMap);
        //Then
        assertNotNull(dto);
        assertEquals(demandToMap.getResourceName(), dto.getResourceName());
        assertEquals(demandToMap.getUserName(), dto.getUserName());
        assertEquals(demandToMap.getDescription(), dto.getDescription());
        assertEquals(demandToMap.getJustification(), dto.getJustification());
        assertEquals(demandToMap.getQuantity(), dto.getQuantity());
    }


}
