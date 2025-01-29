package com.laosarl.allocation_ressources.service.mapper;

import com.laosarl.allocation_ressources.model.DemandDTO;
import com.laosarl.allocation_ressources.domain.Demand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DemandMapper {
    DemandDTO toDemandDTO (Demand demand);
}
