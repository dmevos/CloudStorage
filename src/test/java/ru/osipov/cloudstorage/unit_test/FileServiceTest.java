package ru.osipov.cloudstorage.unit_test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import ru.osipov.cloudstorage.dto.FileDTO;
import ru.osipov.cloudstorage.entities.File;
import ru.osipov.cloudstorage.entities.User;
import ru.osipov.cloudstorage.exceptions.FileException;
import ru.osipov.cloudstorage.exceptions.InputDataException;
import ru.osipov.cloudstorage.exceptions.RepositoryException;
import ru.osipov.cloudstorage.exceptions.TokenException;
import ru.osipov.cloudstorage.repositories.FileRepository;
import ru.osipov.cloudstorage.repositories.UserRepository;
import ru.osipov.cloudstorage.services.FileService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FileServiceTest {
    static FileService fST;
    static int testCount;

    final UserRepository userRepository = Mockito.mock(UserRepository.class);
    final FileRepository fileRepository = Mockito.mock(FileRepository.class);

    private final MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

    private final String login = "user3@mail.com";
    private final String encryptedPassword = "$2a$12$Vvk46VgUMS38f53mMTxj0O6Iql2Du0cAx/arzPQ9ECmrSv0C0cwXW"; //пароль 456
    private final String authToken = "Bearer token";
    private final String filename = "filename";
    private final int limit = 3;


    private final User user = new User(login, encryptedPassword, authToken);
    private final File file = new File(filename, "This is file".getBytes(), "This is file".length(), user);

    @BeforeAll
    public static void startedAll() {
        System.out.println("================= Начало тестирование FileService =================");
    }

    @BeforeEach
    public void InitAndStart() {
        System.out.println("Старт теста №" + ++testCount + " ...");
        fST = new FileService(fileRepository, userRepository);
    }

    @AfterAll
    public static void finishAll() {
        System.out.println("============== Все тесты файлового сервиса завершены ==============");
    }

    @AfterEach
    public void finished() {
        System.out.println("...Тест #" + testCount + " завершен");
        fST = null;
    }

    @Test
    public void testUploadFile() throws IOException {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        // then:
        Assertions.assertTrue(fST.uploadFile(authToken, filename, multipartFile));
    }

    @Test
    public void testUploadFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken)).thenReturn(Optional.of(user));
        // then:
        Assertions.assertThrows(TokenException.class, () -> fST.uploadFile(authToken, filename, multipartFile));
    }

    @Test
    public void testUploadFileInputDataExceptionMultipartFileIsNull() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        // then:
        Assertions.assertThrows(InputDataException.class, () -> fST.uploadFile(authToken, filename, null));
    }

    @Test
    public void testUploadFileInputDataExceptionFilenameIsEmpty() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        // then:
        Assertions.assertThrows(InputDataException.class, () -> fST.uploadFile(authToken, "", multipartFile));
    }

    @Test
    public void testDeleteFile() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser(filename, user)).thenReturn(Optional.of(file));
        // then:
        Assertions.assertTrue(fST.deleteFile(authToken, filename));
    }

    @Test
    public void testDeleteFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenThrow(new TokenException("Ошибка"));
        // then:
        Assertions.assertThrows(TokenException.class, () -> fST.deleteFile(authToken, filename));
    }

    @Test
    public void testDeleteFileFileException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser("", user)).thenReturn(Optional.of(file));
        // then:
        Assertions.assertThrows(FileException.class, () -> fST.deleteFile(authToken, filename));
    }

    @Test
    public void testDeleteFileRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser(filename, user)).thenThrow(new RepositoryException("Ошибка"));
        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fST.deleteFile(authToken, filename));
    }

    @Test
    public void testDownloadFile() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser(filename, user)).thenReturn(Optional.of(file));
        // when:
        File actual = fST.downloadFile(authToken, filename);
        // then:
        Assertions.assertEquals(file, actual);
    }

    @Test
    public void testDownLoadFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenThrow(new TokenException("Ошибка"));
        // then:
        Assertions.assertThrows(TokenException.class, () -> fST.downloadFile(authToken, filename));
    }

    @Test
    public void testDownloadFileRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser("", user)).thenThrow(new RepositoryException("Ошибка"));
        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fST.downloadFile(authToken, ""));
    }

    @Test
    public void testDownloadRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser(filename, user)).thenThrow(new RepositoryException("Ошибка"));
        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fST.downloadFile(authToken, filename));
    }

    @Test
    public void testRenameFile() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser(filename, user)).thenReturn(Optional.of(file));
        // then:
        Assertions.assertTrue(fST.renameFile(authToken, filename, "newFileName"));
    }

    @Test
    public void testRenameFileTokenException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenThrow(new TokenException("Ошибка"));
        // then:
        Assertions.assertThrows(TokenException.class, () -> fST.renameFile(authToken, filename, "newFileName"));
    }

    @Test
    public void testRenameFileRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findFirstByNameAndUser("", user)).thenThrow(new RepositoryException("Ошибка"));
        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fST.renameFile(authToken, "", "newFileName"));
    }

    @Test
    public void testGetList() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findAllFilesByUser(user, PageRequest.of(0, limit))).thenReturn(List.of(file));
        List<FileDTO> expected = List.of(new FileDTO(filename, 12));
        // when:
        List<FileDTO> actual = fST.getList(authToken, limit);
        // then:
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetListTokenException() {
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenThrow(new TokenException("Ошибка"));
        // then:
        Assertions.assertThrows(TokenException.class, () -> fST.getList(authToken, limit));
    }

    @Test
    public void testGetListInputDataException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findAllFilesByUser(user, PageRequest.of(0, limit))).thenThrow(new InputDataException("Ошибка"));
        // then:
        Assertions.assertThrows(InputDataException.class, () -> fST.getList(authToken, -1));
    }

    @Test
    public void testGetListRepositoryException() {
        // given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        Mockito.when(fileRepository.findAllFilesByUser(user, PageRequest.of(0, limit))).thenThrow(new RepositoryException("Ошибка"));
        // then:
        Assertions.assertThrows(RepositoryException.class, () -> fST.getList(authToken, limit));
    }

    @Test
    public void testCheckTokenAndGetUserFromDB() {
        //given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenReturn(Optional.of(user));
        // then:
        Assertions.assertEquals(user, fST.checkTokenAndGetUserFromDB(authToken));
    }

    @Test
    public void testCheckTokenAndGetUserFromDBTokenException() {
        //given:
        Mockito.when(userRepository.findUserByAuthToken(authToken.substring(7))).thenThrow(new TokenException("Ошибка"));
        // then:
        Assertions.assertThrows(TokenException.class, () -> fST.checkTokenAndGetUserFromDB(authToken));
    }

    @Test
    public void testGetFirstFilename() {
        //given:
        Mockito.when(fileRepository.findFirstByNameAndUser(filename, user)).thenReturn(Optional.of(file));
        // then:
        Assertions.assertEquals(file, fST.getFirstFilename(filename, user));
    }

    @Test
    public void testGetFirstFilenameTokenException() {
        //given:
        Mockito.when(fileRepository.findFirstByNameAndUser(filename, user)).thenThrow(new TokenException("Ошибка"));
        // then:
        Assertions.assertThrows(TokenException.class, () -> fST.getFirstFilename(filename, user));
    }
}