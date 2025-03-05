package com.laosarl.allocation_ressources.api;

import com.laosarl.allocation_ressources.model.*;
import com.laosarl.allocation_ressources.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountResource {
    private final AccountService accountService;

    @PostMapping("/admin/auth/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDTO request) {
        accountService.createAccount(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/signupAsAdmin")
    ResponseEntity<Void> signupAsAdmin(@RequestBody SignupRequestDTO request){
        accountService.createAdminAccount(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request){
        LoginResponseDTO response = accountService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset-request")
    public ResponseEntity<Void> requestPasswordReset(PasswordResetRequestDTO request) {
        accountService.createPasswordResetTokenForUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(PasswordResetDTO request) {
        accountService.resetPassword(request);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/user/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        accountService.updateAccount(id, updateUserRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> user = accountService.getAllUsers();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID id) {
        UserDTO userDTO = accountService.getUser(id);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/admin/users/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        accountService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/users/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String name) {
        List<UserDTO> List = accountService.searchUsers(name);
        return ResponseEntity.ok(List);
    }

    @PostMapping("/user/updatePassword")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordDTO request){
        accountService.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}

