package ru.osipov.cloudstorage.i_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.osipov.cloudstorage.dto.FileDTO;
import ru.osipov.cloudstorage.entities.File;
import ru.osipov.cloudstorage.entities.User;
import ru.osipov.cloudstorage.exceptions.FileException;
import ru.osipov.cloudstorage.exceptions.InputDataException;
import ru.osipov.cloudstorage.exceptions.RepositoryException;
import ru.osipov.cloudstorage.exceptions.TokenException;
import ru.osipov.cloudstorage.services.FileService;

import java.io.IOException;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileServiceIntegrationTest {

    private String filename = "filename";
    private final String login = "user3@mail.ru";
    private final String encryptedPassword = "$2a$12$Vvk46VgUMS38f53mMTxj0O6Iql2Du0cAx/arzPQ9ECmrSv0C0cwXW"; //пароль 456
    private final String token = "token";
    private final String invalidToken = "invalidToken";

    private final User user = new User(3, login, encryptedPassword, token);
    private final File fileTwo = new File(2, "This is file two".getBytes(), "This is file two".length(), filename + "2", user);

    @Autowired
    FileService fileService;

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
    public void testUploadFile() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("file", "filename zero", "text/plain", "file content".getBytes());
        // then:
        Assertions.assertTrue(fileService.uploadFile("Bearer " + token, filename + " zero", multipartFile));
    }

    @Test
    public void testUploadFileTokenException() {
        // given:
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.uploadFile(invalidToken, filename, multipartFile));
    }

    @Test
    public void testUploadFileInputDataExceptionMultipartFileIsNull() {
        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.uploadFile("Bearer " + token, filename, null));
    }

    @Test
    public void testUploadFileInputDataExceptionFilenameIsEmpty() {
        // given
        filename = "";
        MultipartFile multipartFile = new MockMultipartFile("file", "", "text/plain", "file content".getBytes());
        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.uploadFile("Bearer " + token, "", multipartFile));
    }

    @Test
    public void testUploadFileRepositoryException() {
        // given
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fileService.uploadFile("Bearer " + token, filename, multipartFile));
    }

    @Test
    public void testDeleteFile() {
        Assertions.assertTrue(fileService.deleteFile("Bearer " + token, filename + "1"));
    }

    @Test
    public void testDeleteFileTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.deleteFile(invalidToken, filename));
    }

    @Test
    public void testDeleteFileFileException() {
        // given
        filename = "anotherFilename";
        // then:
        Assertions.assertThrows(FileException.class, () -> fileService.deleteFile("Bearer " + token, filename));
        Assertions.assertThrows(FileException.class, () -> fileService.deleteFile("Bearer " + token, ""));
    }

    @Test
    public void testDownloadFile() {
        // when:
        File actual = fileService.downloadFile("Bearer " + token, filename + "2");
        // then:
        Assertions.assertEquals(fileTwo, actual);
    }

    @Test
    public void testDownloadFileTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.downloadFile(invalidToken, filename));
    }

    @Test
    public void testDownloadFileFileException() {
        // given:
        filename = "anotherFilename";
        // then:
        Assertions.assertThrows(FileException.class, () -> fileService.downloadFile("Bearer " + token, filename));
        Assertions.assertThrows(FileException.class, () -> fileService.downloadFile("Bearer " + token, ""));
    }

    @Test
    public void testRenameFile() {
        // when:
        boolean actual = fileService.renameFile("Bearer " + token, filename + "3", "new_name");
        // then:
        Assertions.assertTrue(actual);
    }

    @Test
    public void testRenameFileTokenException() {
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.renameFile(invalidToken, filename, "newFileName"));
    }

    @Test
    public void testRenameFileFileException() {
        // then:
        Assertions.assertThrows(FileException.class, () -> fileService.renameFile("Bearer " + token, "", "newFileName"));
        Assertions.assertThrows(FileException.class, () -> fileService.renameFile("Bearer " + token, filename, "newFileName"));
    }

    @Test
    public void testGetList() {
        // given:
        Integer limit = 1;
        List<FileDTO> expected = List.of(new FileDTO(filename + "1", 16));
        // when:
        List<FileDTO> actual = fileService.getList("Bearer " + token, limit);
        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetListTokenException() {
        // given:
        Integer limit = 1;
        // then:
        Assertions.assertThrows(TokenException.class, () -> fileService.getList(invalidToken, limit));
    }

    @Test
    public void testGetListInputDataException() {
        // given:
        Integer limit = -1;
        // then:
        Assertions.assertThrows(InputDataException.class, () -> fileService.getList("Bearer " + token, limit));
    }
}
