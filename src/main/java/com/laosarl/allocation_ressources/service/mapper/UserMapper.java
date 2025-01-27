package com.laosarl.allocation_ressources.service.mapper;


import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.AuthenticationRequestDTO;
import com.laosarl.allocation_ressources.model.AuthenticationResponseDTO;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User authRequestToUser(AuthenticationRequestDTO dto);

    AuthenticationResponseDTO userToAuthResponse(User user, String token);

    UserDTO toUserDTO (User user);

    User createAccountRequestToUser(SignupRequestDTO request);
}