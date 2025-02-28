package com.laosarl.allocation_ressources.service.mapper;


import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.UserDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id")
    @Mapping(target = "name")
    @Mapping(target = "surname")
    @Mapping(target = "email")
    UserDTO toUserDTO(User user);

}