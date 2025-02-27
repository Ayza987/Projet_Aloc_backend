package com.laosarl.allocation_ressources.service.mapper;


import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.model.ResourceForUserDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    ResourceDTO toResourceDTO(Resource resource);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResourceFromDto(ResourceDTO dto, @MappingTarget Resource resource);

    ResourceDTO toDto(Resource updatedResource);

    ResourceForUserDTO toResourceForUserDTO(Resource resource);
}
