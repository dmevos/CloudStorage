package ru.osipov.cloudstorage.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.osipov.cloudstorage.dto.UserDTO;
import ru.osipov.cloudstorage.entities.User;
import ru.osipov.cloudstorage.exceptions.AuthException;
import ru.osipov.cloudstorage.exceptions.TokenException;
import ru.osipov.cloudstorage.models.Login;
import ru.osipov.cloudstorage.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public Login login(UserDTO userDTO) {

        List<User> usersFromDB = userRepository.findByUsername(userDTO.getLogin());

        if (usersFromDB.isEmpty()) throw new AuthException("Неверное имя пользователя");

        for (User user : usersFromDB) {
            if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                user.setAuthToken(UUID.randomUUID().toString());
                userRepository.saveAndFlush(user);
                log.info("Аутентификация прошла успешно");
                return new Login(user.getAuthToken());
            }
        }
        throw new AuthException("Неверный пароль для пользователя: " + userDTO.getLogin());
    }

    public Login logout(String authToken) {
        Optional<User> userResult = userRepository.findUserByAuthToken(authToken.substring(7));
        if (userResult.isPresent()) {
            User user = userResult.get();
            user.setAuthToken(null);
            userRepository.saveAndFlush(user);
            log.info("Пользователь {} разлогинился", user.getUsername());
            return new Login(user.getAuthToken());
        } else {
            throw new TokenException("Токен ошибочен");
        }
    }
}