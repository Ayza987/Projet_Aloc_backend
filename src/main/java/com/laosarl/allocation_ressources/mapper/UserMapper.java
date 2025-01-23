package com.laosarl.allocation_ressources.mapper;


import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.AuthenticationRequestDTO;
import com.laosarl.allocation_ressources.model.AuthenticationResponseDTO;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.SignupResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
User authRequestToUser(AuthenticationRequestDTO dto);
AuthenticationResponseDTO userToAuthResponse(User user, String token);

User createAccountRequestToUser(SignupRequestDTO request);
SignupResponseDTO toSignUpResponse(String code, String message);
}