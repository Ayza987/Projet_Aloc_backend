package com.laosarl.allocation_ressources.service.mapper;

import com.laosarl.allocation_ressources.domain.AllocatedResource;
import com.laosarl.allocation_ressources.model.AllocatedResourceDTO;
import com.laosarl.allocation_ressources.model.DemandDTO;
import com.laosarl.allocation_ressources.domain.Demand;
import com.laosarl.allocation_ressources.model.UpdateDemandDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DemandMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id")
    @Mapping(target = "userName")
    @Mapping(target = "userEmail")
    @Mapping(target = "resourceName")
    @Mapping(target = "description")
    @Mapping(target = "justification")
    @Mapping(target = "urgency")
    @Mapping(target = "dueDate")
    @Mapping(target = "quantity")
    @Mapping(target = "status")
    @Mapping(target = "dateTime")
    DemandDTO toDemandDTO (Demand demand);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id")
    @Mapping(target = "demandId")
    @Mapping(target = "userEmail")
    @Mapping(target = "resourceName")
    @Mapping(target = "quantity")
    @Mapping(target = "demandDate")
    @Mapping(target = "allocationDate")
    @Mapping(target = "status")
    AllocatedResourceDTO   toAllocatedResourceDTO (AllocatedResource allocatedResource);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDemandFromDto(UpdateDemandDTO dto, @MappingTarget Demand demand);

}
