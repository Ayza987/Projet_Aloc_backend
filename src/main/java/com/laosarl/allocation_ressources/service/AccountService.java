package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.UpdateUserRequestDTO;
import com.laosarl.allocation_ressources.model.UserDTO;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void createAccount(SignupRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        userRepository.save(User.builder().email(request.getEmail()).name(request.getName()).surname(request.getSurname()).password(generateSecurePassword()).build());
    }

    public String generateSecurePassword() {
        StringBuilder password = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            password.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return password.toString();
    }

    public void updateAccount(Long id, UpdateUserRequestDTO updateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getSurname() != null) {
            user.setSurname(updateRequest.getSurname());
        }

        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(userMapper::toUserDTO).toList();
    }

    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No user found"));
        return userMapper.toUserDTO(user);
    }


    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("No user found"));

        userRepository.delete(user);
    }

}
