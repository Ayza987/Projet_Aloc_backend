package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.mapper.UserMapper;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.SignupResponseDTO;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock
    private ArgumentCaptor<User> userCaptor;

    @InjectMocks
    private AccountService accountService;

    @Test
    void CreateAccountAndSendEmail() {
        SignupRequestDTO request = new SignupRequestDTO();
            request.setEmail("test@test.com");
            request.setName("Test User");
            request.setSurname("User");


        User userMapped = new User();
             userMapped.setEmail(request.getEmail());
             userMapped.setName(request.getName());
             userMapped.setSurname(request.getSurname());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userMapper.createAccountRequestToUser(request)).thenReturn(userMapped);

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        when(passwordEncoder.encode(passwordCaptor.capture())).thenReturn("encoded_password");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer( invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });

        SignupResponseDTO response = accountService.createAccount(request);

        verify(userRepository).save(userCaptor.capture());
        User userCreated = userCaptor.getValue();

        assertEquals(request.getName(), userCreated.getName());
        assertEquals(request.getSurname(), userCreated.getSurname());
        assertEquals(request.getEmail(), userCreated.getEmail());
        assertEquals("encoded_password", userCreated.getPassword());
        assertFalse(userCreated.getIsAdmin());

       String passwordGenerated = passwordCaptor.getValue();
//
          assertNotNull(passwordGenerated);
          assertTrue(passwordGenerated.length() >= 8);
          assertTrue(passwordGenerated.matches(".*[A-Z].*"));
          assertTrue(passwordGenerated.matches(".*[0-9].*"));
//
 //       assertNotNull(response);
//        assertEquals("200", response.getCode());
//        assertNotNull(response.getMessage());


    }

}
