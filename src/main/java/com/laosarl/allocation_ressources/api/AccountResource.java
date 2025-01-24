package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.SignupResponseDTO;
import com.laosarl.allocation_ressources.model.UpdateUserRequestDTO;
import com.laosarl.allocation_ressources.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
public class AccountResource {
    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        accountService.createAccount(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDTO updateUserRequestDTO
    ) {
       accountService.updateAccount(id, updateUserRequestDTO);
       return ResponseEntity.ok().build();
    }
}

