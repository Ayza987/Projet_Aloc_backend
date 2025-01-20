package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.mapper.UserMapper;
import com.laosarl.allocation_ressources.model.AuthenticationRequestDTO;
import com.laosarl.allocation_ressources.model.AuthenticationResponseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;


class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void authRequestToUser_ShouldMapFieldsCorrectly() {
        // Given
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("test@test.com", "password");

        // When
        User user = userMapper.authRequestToUser(requestDTO);

        // Then
        assertNotNull(user);
        assertEquals(requestDTO.getEmail(), user.getEmail());
        assertEquals(requestDTO.getPassword(), user.getPassword());
    }

    @Test
    void userToAuthResponse_ShouldMapFieldsCorrectly() {
        // Given
        User user = User.builder()
                .email("test@test.com")
                .isAdmin(true)
                .build();
        String token = "jwt_token";

        // When
        AuthenticationResponseDTO responseDTO = userMapper.userToAuthResponse(user, token);

        // Then
        assertNotNull(responseDTO);
        assertEquals(token, responseDTO.getToken());
        assertEquals(user.getIsAdmin(), responseDTO.getIsAdmin());
    }
}
