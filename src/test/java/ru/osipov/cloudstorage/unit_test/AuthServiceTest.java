package ru.osipov.cloudstorage.unit_test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.osipov.cloudstorage.dto.UserDTO;
import ru.osipov.cloudstorage.entities.User;
import ru.osipov.cloudstorage.exceptions.AuthException;
import ru.osipov.cloudstorage.models.Login;
import ru.osipov.cloudstorage.repositories.UserRepository;
import ru.osipov.cloudstorage.services.AuthService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AuthServiceTest {
    static AuthService aST;
    static int testCount;

    UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final String login = "user3@mail.com";
    private final String password = "456";
    private final String encryptedPassword = "$2a$12$Vvk46VgUMS38f53mMTxj0O6Iql2Du0cAx/arzPQ9ECmrSv0C0cwXW"; //пароль 456
    private final String authToken = "Bearer token";

    @BeforeAll
    public static void startedAll() {
        System.out.println("==================== Начало тестирование AuthService ===================");
    }

    @BeforeEach
    public void InitAndStart() {
        System.out.println("Старт теста №" + ++testCount + " ...");
        aST = new AuthService(userRepository, new BCryptPasswordEncoder());
    }

    @AfterAll
    public static void finishAll() {
        System.out.println("============== Все тесты сервиса аутентификации завершены ==============");
    }

    @AfterEach
    public void finished() {
        System.out.println("...Тест #" + testCount + " завершен");
        aST = null;
    }

    @Test
    public void testLoginOk() {
        // given:
        User user = new User(login, encryptedPassword, authToken);
        UserDTO userDTO = new UserDTO(login, password);
        Mockito.when(userRepository.findByUsername(login)).thenReturn(List.of(user));
        // when:
        Login actual = aST.login(userDTO);
        // then:
        Assertions.assertSame(Login.class, actual.getClass());
        Assertions.assertNotNull(actual.getAuthToken());
        Assertions.assertNotEquals(0, actual.getAuthToken().length());
    }

    @Test
    public void testLoginAuthException() {
        // given:
        Mockito.when(userRepository.findByUsername("NOT@not.not")).thenReturn(Collections.emptyList());
        // then:
        Assertions.assertThrows(AuthException.class, () -> aST.login(new UserDTO("NOT@not.not", password)));
    }

    @Test
    public void testLogout() {
        // given:
        Optional<User> optionalUser = Optional.of(new User(login, encryptedPassword, authToken));
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(optionalUser);
        User expected = Optional.of(new User(login, encryptedPassword, null)).get();
        // when:
        aST.logout(authToken);
        User actual = optionalUser.get();
        // then:
        Assertions.assertEquals(expected, actual);
    }
}