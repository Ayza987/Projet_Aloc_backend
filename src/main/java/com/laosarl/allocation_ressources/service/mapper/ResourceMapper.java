package com.laosarl.allocation_ressources.service.mapper;


import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.model.ResourceForUserDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id")
    @Mapping(target = "name")
    @Mapping(target = "type")
    @Mapping(target = "description")
    @Mapping(target = "quantity")
    @Mapping(target = "isAvailable")
    ResourceDTO toResourceDTO(Resource resource);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateResourceFromDto(ResourceDTO dto, @MappingTarget Resource resource);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id")
    @Mapping(target = "name")
    @Mapping(target = "type")
    @Mapping(target = "description")
    ResourceForUserDTO toResourceForUserDTO(Resource resource);
}
