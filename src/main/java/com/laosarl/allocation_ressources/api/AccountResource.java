package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.SignupResponseDTO;
import com.laosarl.allocation_ressources.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AccountResource {
    private final AccountService accountService;

    @PostMapping("/signup")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SignupResponseDTO> signup(
            @RequestBody SignupRequestDTO request){
        return ResponseEntity.ok(accountService.createAccount(request));
    }
}

