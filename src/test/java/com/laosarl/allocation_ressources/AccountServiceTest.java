//package com.laosarl.allocation_ressources;
//
//import com.laosarl.allocation_ressources.domain.User;
//import com.laosarl.allocation_ressources.mapper.UserMapper;
//import com.laosarl.allocation_ressources.repository.UserRepository;
//import com.laosarl.allocation_ressources.model.SignupRequestDTO;
//import com.laosarl.allocation_ressources.service.AccountService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//class AccountServiceTest {
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JavaMailSender mailSender;
//
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private AccountService accountService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void createAccount_WhenEmailDoesNotExist_ShouldCreateAccount() {
//        // Arrange
//        when(userRepository.findByEmail(SignupRequestDTO.getEmail())).thenReturn(Optional.empty());
//        when(userMapper.createAccountRequestToUser(SignupRequestDTO)).thenReturn(mappedUser);
//        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(mappedUser);
//
//        // Act
//        User result = accountService.createAccount(SignupRequestDTO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(SignupRequestDTO.getEmail(), result.getEmail());
//        assertFalse(result.isAdmin());
//        verify(emailSender).send(any(SimpleMailMessage.class));
//        verify(userRepository).save(any(User.class));
//    }