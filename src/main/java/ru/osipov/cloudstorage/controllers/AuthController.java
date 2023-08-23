package ru.osipov.cloudstorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.osipov.cloudstorage.dto.UserDTO;
import ru.osipov.cloudstorage.models.Login;
import ru.osipov.cloudstorage.services.AuthService;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Login login(@RequestBody UserDTO userDTO) {
        return authService.login(userDTO);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("auth-token") String authToken) {
        authService.logout(authToken);
    }
}