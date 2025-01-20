package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.AuthenticationRequestDTO;
import com.laosarl.allocation_ressources.model.AuthenticationResponseDTO;
import com.laosarl.allocation_ressources.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")

public class AuthenticationResource {
    private final AuthenticationService authenticationService;

    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(
            @RequestBody AuthenticationRequestDTO request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
