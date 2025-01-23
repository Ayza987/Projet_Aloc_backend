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
import java.util.UUID;

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
        String generatedPassword = this.generateSecurePassword();
        newUser.setPassword(passwordEncoder.encode(generatedPassword));
        newUser.setIsAdmin(false);

        User savedUser = userRepository.save(newUser);
        this.sendAccountCreationEmail(savedUser.getEmail(), generatedPassword);

        return userMapper.toSignUpResponse("200", "User created successfully");
    }

    public String generateSecurePassword() {
        StringBuilder password = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            password.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return password.toString();
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
