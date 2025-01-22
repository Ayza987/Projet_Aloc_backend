package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.SignupResponseDTO;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JavaMailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public SignupResponseDTO createAccount(SignupRequestDTO request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException("Email already exists");
                });

        User newUser = userMapper.createAccountRequestToUser(request);
        String generatedPassword = generateSecurePassword();
        newUser.setPassword(passwordEncoder.encode(generatedPassword));
        newUser.setIsAdmin(false);

        User savedUser = userRepository.save(newUser);
        sendAccountCreationEmail(savedUser.getEmail(), generatedPassword);

        return userMapper.toSignUpResponse("200", "User created successfully");
    }

    public String generateSecurePassword() {
        StringBuilder password = new StringBuilder(8);
        password.append(CHARS.substring(0, 26).charAt(RANDOM.nextInt(26)));
        password.append(CHARS.substring(52).charAt(RANDOM.nextInt(10)));

        for (int i = 0; i < 6; i++) {
            password.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = RANDOM.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }

        return new String(passwordArray);
    }

    public void sendAccountCreationEmail(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Création de votre compte");
        message.setText("""
            Bonjour,
            
            Votre compte a été créé avec succès.
            
            Vos identifiants de connexion :
            Email : %s
            Mot de passe : %s
            
            Nous vous recommandons de changer votre mot de passe lors de votre première connexion.
            
            Cordialement,
            L'équipe Dev
            """.formatted(email, password));

        emailSender.send(message);
    }
}
