package ru.osipov.cloudstorage.i_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.osipov.cloudstorage.dto.UserDTO;
import ru.osipov.cloudstorage.exceptions.AuthException;
import ru.osipov.cloudstorage.exceptions.TokenException;
import ru.osipov.cloudstorage.models.Login;
import ru.osipov.cloudstorage.repositories.UserRepository;
import ru.osipov.cloudstorage.services.AuthService;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthServiceIntegrationTest {
    private final String login = "user4@mail.ru";
    private final String invalidToken = "invalidToken";

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @Container
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("cloud_database_test")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("test_db.sql");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

    @Test
    public void testLogin() {
        //given
        String password = "123";
        UserDTO userDTO = new UserDTO(login, password);
        String token = "token2";
        Login expected = new Login(token);
        // when:
        Login actual = authService.login(userDTO);
        // then:
        Assertions.assertSame(expected.getClass(), actual.getClass());
        Assertions.assertNotNull(actual.getAuthToken());
        Assertions.assertNotEquals(0, actual.getAuthToken().length());
    }

    @Test
    public void testLoginAuthException() {
        //given
        String invalidPassword = "invalidPassword";
        UserDTO userDTO = new UserDTO(login, invalidPassword);
        // then:
        Assertions.assertThrows(AuthException.class, () -> authService.login(userDTO));
    }

    @Test
    public void testLogout() {
        // then:
        Assertions.assertSame(Login.class, authService.logout("Bearer token").getClass());
    }

    @Test
    public void testLogoutTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> authService.logout(invalidToken));
    }
}
