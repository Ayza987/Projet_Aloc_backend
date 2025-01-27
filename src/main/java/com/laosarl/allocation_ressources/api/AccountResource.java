package com.laosarl.allocation_ressources.api;


import com.laosarl.allocation_ressources.model.SignupRequestDTO;
import com.laosarl.allocation_ressources.model.SignupResponseDTO;
import com.laosarl.allocation_ressources.model.UpdateUserRequestDTO;
import com.laosarl.allocation_ressources.model.UserDTO;
import com.laosarl.allocation_ressources.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class AccountResource {
    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO request) {
        accountService.createAccount(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        accountService.updateAccount(id, updateUserRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> user = accountService.getAllUsers();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO userDTO = accountService.getUser(id);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        accountService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}

