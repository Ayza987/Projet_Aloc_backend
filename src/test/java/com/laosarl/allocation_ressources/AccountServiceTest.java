package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.AccountService;
import com.laosarl.allocation_ressources.service.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AccountService objectUnderTest;

    @Test
    void createAccount_whenAllConditionsAreMet_returnsSuccessResponse() {
        //Given
        objectUnderTest = spy(objectUnderTest);
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO().email("test@gmail.com").name("john").surname("doe");
        String generatedPassword = "secure_password";

        when(userRepository.existsByEmail(signupRequestDTO.getEmail())).thenReturn(false);
        when(objectUnderTest.generateSecurePassword()).thenReturn(generatedPassword);

        //When
        objectUnderTest.createAccount(signupRequestDTO);
        //Then
        verify(userRepository).save(argThat(user -> user.getEmail().equals(signupRequestDTO.getEmail()) && user.getName().equals(signupRequestDTO.getName()) && user.getSurname().equals(signupRequestDTO.getSurname()) && user.getPassword().equals(generatedPassword)));

    }

    @Test
    void createAccount_whenEmailexists_ShouldThrowException() {
        //Given
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO().email("test@123.com").surname("john").name("doe");

        when(userRepository.existsByEmail(signupRequestDTO.getEmail())).thenReturn(true);

        //When//Then
        assertThrows(RuntimeException.class, () -> objectUnderTest.createAccount(signupRequestDTO));
        verify(userRepository, never()).save(any(User.class));
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
