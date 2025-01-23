package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.mapper.UserMapper;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.SignupResponseDTO;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest{
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JavaMailSender emailSender;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService objectUnderTest;

    @Test
    void createAccount_whenAllConditionsAreMet_returnsSuccessResponse() {
        //GIVEN
        var code = "200";
        var message = "User created successfully";
        var expected = mock(SignupResponseDTO.class);
        objectUnderTest = spy(objectUnderTest);
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO()
                .email("azerty")
                .name("jaures")
                .surname("passwordEncode");
        User userToCreate = mock(User.class);
        String passwordGenereted = "Jaures06";
        String passwordEncode = "passwordEncode";
        userToCreate.setPassword(passwordEncode);
        userToCreate.setIsAdmin(false);
        var userToSave = mock(User.class);


        when(userRepository.findByEmail(signupRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(userMapper.createAccountRequestToUser(signupRequestDTO)).thenReturn(userToCreate);
        when(objectUnderTest.generateSecurePassword()).thenReturn(passwordGenereted);
        when(passwordEncoder.encode(passwordGenereted)).thenReturn(passwordEncode);
        when(userRepository.save(userToCreate)).thenReturn(userToSave);
        when(userMapper.toSignUpResponse(code, message)).thenReturn(expected);

        //WHEN
        var resultUnderTest = objectUnderTest.createAccount(signupRequestDTO);

        //THEN
        verify(objectUnderTest).sendAccountCreationEmail(userToSave.getEmail(), passwordGenereted);
        verify(userRepository).save(userToCreate);
        assertThat(resultUnderTest)
                .isEqualTo(expected);
    }

    @Test
    void generateSecurePassword_shouldProduceValidPassword() {
        String generatedPassword = objectUnderTest.generateSecurePassword();

        assertNotNull(generatedPassword);
        assertEquals(8, generatedPassword.length());

        assertTrue(generatedPassword.matches("^(?=.*[a-z])(?=.*[0-9]).{8}$"));

        Set<String> passwords = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            passwords.add(objectUnderTest.generateSecurePassword());
        }
        assertTrue(passwords.size() > 20, "Passwords should be random");
    }

}
