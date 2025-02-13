package com.laosarl.allocation_ressources.service.mapper;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.model.AllocatedResourceDTO;
import com.laosarl.allocation_ressources.model.DemandDTO;
import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.UpdateDemandDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DemandMapper {
    DemandDTO toDemandDTO (Demand demand);
    AllocatedResourceDTO toAllocatedResourceDTO (AllocatedResource allocatedResource);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDemandFromDto(UpdateDemandDTO dto, @MappingTarget Demand demand);

}
