package com.laosarl.allocation_ressources.service;


import com.laosarl.allocation_ressources.mapper.UserMapper;
import com.laosarl.allocation_ressources.model.AuthenticationRequestDTO;
import com.laosarl.allocation_ressources.model.AuthenticationResponseDTO;
import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid credentials");
    }
     String token = jwtService.generateToken(user);
        return userMapper.userToAuthResponse(user, token);
    }
}
