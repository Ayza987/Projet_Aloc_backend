package com.laosarl.allocation_ressources.service.mapper;


import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDTO(User user);

}