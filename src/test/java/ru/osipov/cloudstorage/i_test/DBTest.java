package ru.osipov.cloudstorage.i_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.osipov.cloudstorage.entities.File;
import ru.osipov.cloudstorage.entities.User;
import ru.osipov.cloudstorage.repositories.FileRepository;
import ru.osipov.cloudstorage.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DBTest {
    private final String filename = "filename";
    private final String login = "user3@mail.ru";
    private final String encryptedPassword = "$2a$12$Vvk46VgUMS38f53mMTxj0O6Iql2Du0cAx/arzPQ9ECmrSv0C0cwXW"; //пароль 456
    private final String authToken = "token";

    private final User user = new User(3, login, encryptedPassword, authToken);
    private final File fileOne = new File(1, "This is file one".getBytes(), "This is file one".length(), filename + "1", user);
    private final File fileTwo = new File(2, "This is file two".getBytes(), "This is file two".length(), filename + "2", user);
    private final File fileThree = new File(3, "This is file three".getBytes(), "This is file three".length(), filename + "3", user);

    @Autowired
    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;

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
    public void contextDatabase() {
        Assertions.assertTrue(database.isRunning());
    }

    @Test
    public void testGetUserByLogin() {
        //given
        List<User> expected = List.of(user);
        // when:
        List<User> actual = userRepository.findByUsername("user3@mail.ru");
        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetUserByAuthToken() {
        //given
        Optional<User> expected = Optional.of(user);
        // when:
        Optional<User> actual = userRepository.findUserByAuthToken(authToken);
        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetFileByFilenameAndUser() {
        //given
        Optional<File> expected = Optional.of(fileOne);
        // when:
        Optional<File> actual = fileRepository.findFirstByNameAndUser(filename + "1", user);
        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetFile() {
        //given
        List<File> expected = List.of(fileOne, fileTwo, fileThree);
        // when:
        List<File> actual = fileRepository.findAllFilesByUser(user, PageRequest.of(0, 3));
        // then:
        Assertions.assertEquals(expected, actual);
    }
}