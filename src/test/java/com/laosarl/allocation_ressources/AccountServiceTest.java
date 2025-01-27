package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.UpdateUserRequestDTO;
import com.laosarl.allocation_ressources.model.UserDTO;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.AccountService;
import com.laosarl.allocation_ressources.service.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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
    void updateAccount_shouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@email.com");
        existingUser.setName("Old Name");

        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO();
        updateRequest.setEmail("new@email.com");
        updateRequest.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // When
        objectUnderTest.updateAccount(userId, updateRequest);

        // Then
        verify(userRepository).save(existingUser);
        assertEquals("new@email.com", existingUser.getEmail());
        assertEquals("New Name", existingUser.getName());
    }

    @Test
    void getAllUsers_WhenUsersAreFound() {
        // Given

        List<User> usersList = List.of(User.builder().name("John").surname("Doe").email("johndoe@gmail.com").build(), User.builder().name("Tati").surname("Gali").email("tatigali@gmail.com").build()

        );
        when(userRepository.findAll()).thenReturn(usersList);
        // When
        objectUnderTest.getAllUsers();
        // Then
        assertThat(usersList).hasSize(2);
    }

    @Test
    void getAllUsers_ShouldThrowException_WhenUsersAreNotFound(){
        //Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.getAllUsers());
    }

    @Test
    void updateAccount_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.updateAccount(userId, updateRequest));
    }

    @Test
    void generateSecurePassword_shouldProduceValidPassword() {
        String generatedPassword = objectUnderTest.generateSecurePassword();

        assertNotNull(generatedPassword);
        assertEquals(8, generatedPassword.length());

        assertTrue(generatedPassword.matches("^(?=.*[a-z])(?=.*[0-9]).{8}$"));
    }

    @Test
    void getUser_WhenIdIsFound() {
        //Given
        Long userId = 3L;
        User existingUser = User.builder().email("johndoe@gmail.com").name("John").surname("doe").build();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(3L);
        userDTO.setName("John");
        userDTO.setSurname("Doe");
        userDTO.setEmail("johndoe@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toUserDTO(existingUser)).thenReturn(userDTO);

        //When
        UserDTO expectedDTO = objectUnderTest.getUser(userId);
        //Then
        assertThat(userDTO).isEqualTo(expectedDTO);
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        //Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //Act & Assert

        assertThrows(RuntimeException.class, () -> objectUnderTest.getUser(userId));


    }

    @Test
    void deleteUser_WhenIdIsFound() {
        //Given
        Long userId = 1L;
        User existingUser = User.builder().email("johndoe@gmail.com").name("John").surname("Doe").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        //When
        objectUnderTest.deleteUser(userId);
        //Then
        verify(userRepository).delete(existingUser);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserIsNotFound() {
        //Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //Act & Assert
        assertThrows(RuntimeException.class, () -> objectUnderTest.deleteUser(userId));

    }

}
