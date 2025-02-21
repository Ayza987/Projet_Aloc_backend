package com.laosarl.allocation_ressources.service;

import com.laosarl.allocation_ressources.configurations.JwtUtil;
import com.laosarl.allocation_ressources.domain.PasswordResetToken;
import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.exceptions.*;
import com.laosarl.allocation_ressources.model.*;
import com.laosarl.allocation_ressources.repository.PasswordResetTokenRepository;
import com.laosarl.allocation_ressources.repository.UserRepository;
import com.laosarl.allocation_ressources.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z0-9]{8,}$");
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void createAccount(SignupRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        String generatedPassword = generateSecurePassword();
        String encodedPassword = passwordEncoder.encode(generatedPassword);

        userRepository.save(User.builder().email(request.getEmail()).name(request.getName()).surname(request.getSurname()).password(encodedPassword).build());

        String subject = "Nouveau compte Aloc";
        String body = "Bonjour" + " " + request.getName() + " " + request.getSurname() + "," + " "+ "votre compte Aloc vient d'être créé.\n"
                + "Vos identifiants de connexion sont les suivants : " + "\n"
                + "Email : " + request.getEmail() + "\n"
                + "Mot de passe : " + generatedPassword + "\n\n"
                + "Il vous est recommandé de changer ce mot de passe lors de votre première connexion."+ "\n\n\n"
                + "Cordialement.";

        emailService.sendEmail(request.getEmail(), subject, body);
    }

    public String generateSecurePassword() {
        StringBuilder password = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            password.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }

        return password.toString();
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ObjectNotFoundException("User not found"));
            String token = jwtUtil.generateToken(user.getEmail(), user.getIsAdmin());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setIsAdmin(user.getIsAdmin());

            return response;

        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }


    @Transactional
    public void createPasswordResetTokenForUser(PasswordResetRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ObjectNotFoundException("User not found "));

        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken =  PasswordResetToken.builder().token(token).user(user).build();
        passwordResetTokenRepository.save(myToken);

        String subject = "Réinitialisation de mot de passe";
        String resetUrl = "http://aloc.com/reset-password?token=" + token;
        String body = String.format("""
            Bonjour %s %s,
            
            Vous avez demandé la réinitialisation de votre mot de passe.
            Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :
            %s
            
            Ce lien est valable pendant 24 heures.
            
            Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.
            
            Cordialement.
            """, user.getName(), user.getSurname(), resetUrl);

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void validatePasswordResetToken(String token) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        if (resetToken == null) {
            throw new InvalidTokenException("Invalid token");
        }

        if (resetToken.getExpiryDate().before(new Date())) {
            throw new TokenExpiredException("Token has expired");
        }
    }

    @Transactional
    public void resetPassword(PasswordResetDTO request) {
        validatePasswordResetToken(request.getToken());
        if (!PASSWORD_PATTERN.matcher(request.getNewPassword()).matches()) {
            throw new InvalidPasswordFormatException("Password does not respect constraints");
        }
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken());
        User user = resetToken.getUser();
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }


    public void updateAccount(UUID id, UpdateUserRequestDTO updateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));

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

    public UserDTO getUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("No user found"));
        return userMapper.toUserDTO(user);
    }


    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("No user found"));

        userRepository.delete(user);
    }

    public List<UserDTO> searchUsers(String name) {
        List<User> userList = userRepository.findByNameContainingIgnoreCase(name);

        if(userList.isEmpty()){
            throw new NoResultsFoundException("No results found");
        }
        return userList.stream().map(userMapper::toUserDTO).toList();
    }


}
