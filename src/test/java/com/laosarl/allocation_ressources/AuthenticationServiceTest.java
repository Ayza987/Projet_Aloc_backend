package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.mapper.UserMapper;
import com.laosarl.allocation_ressources.model.AuthenticationRequestDTO;
import com.laosarl.allocation_ressources.model.AuthenticationResponseDTO;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.AuthenticationService;
import com.laosarl.allocation_ressources.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() {
        // Given
        AuthenticationRequestDTO request = new AuthenticationRequestDTO();
        request.setEmail("test@test.com");
        request.setPassword("password");

        User user = User.builder()
                .email("test@test.com")
                .password("encoded_password")
                .isAdmin(false)
                .build();

        String expectedToken = "jwt_token";

        AuthenticationResponseDTO expectedResponse = new AuthenticationResponseDTO();
        expectedResponse.setToken(expectedToken);
        expectedResponse.setIsAdmin(false);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(expectedToken);
        when(userMapper.userToAuthResponse(user, expectedToken)).thenReturn(expectedResponse);

        // When
        AuthenticationResponseDTO response = authenticationService.authenticate(request);

        System.out.println("Authentication Response: " + response);

        // Then
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals(false, response.getIsAdmin());
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldThrowException() {
        // Given
        AuthenticationRequestDTO request = new AuthenticationRequestDTO("test@test.com", "wrong_password");
        User user = User.builder()
                .email("test@test.com")
                .password("encoded_password")
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class,
                () -> authenticationService.authenticate(request));
    }
}