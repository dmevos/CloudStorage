package ru.osipov.cloudstorage.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public List<FileDTO> getList(String authToken, Integer limit) {

        if (limit < 0) throw new InputDataException("Значение лимита ошибочно");

        log.info("Создается список файлов...");
        User user = checkTokenAndGetUserFromDB(authToken);

        List<File> fileList = fileRepository.findAllFilesByUser(user, PageRequest.of(0, limit));

        List<FileDTO> fileDTOList = new ArrayList<>();
        for (File file : fileList) {
            fileDTOList.add(new FileDTO(file.getName(), file.getSize()));
        }
        log.info("Список файлов создан");
        return fileDTOList;
    }

    public boolean uploadFile(String authToken, String filename, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty() || filename == null || filename.isEmpty())
            throw new InputDataException("Ошибка при передаче файла");

        User user = checkTokenAndGetUserFromDB(authToken);

        File fileEntity = new File(filename, file.getBytes(), (int) file.getSize(), user);
        try {
            fileRepository.saveAndFlush(fileEntity);
        } catch (RuntimeException e) {
            throw new RepositoryException("Ошибка сохранения файла в БД>");
        }
        log.info("Файл {} загружен", filename);
        return true;
    }

    public boolean deleteFile(String authToken, String filename) {
        User user = checkTokenAndGetUserFromDB(authToken);
        File file = getFirstFilename(filename, user);
        fileRepository.delete(file);
        log.info("Файл {} удален", filename);
        return true;
    }

    public File downloadFile(String authToken, String filename) {
        User user = checkTokenAndGetUserFromDB(authToken);
        File file = getFirstFilename(filename, user);
        if (file == null)
            throw new NullPointerException("Ошибка скачивания файла!");

        log.info("Файл {} скачен", filename);
        return file;
    }

    public boolean renameFile(String authToken, String filename, String newFileName) {
        User user = checkTokenAndGetUserFromDB(authToken);
        File file = getFirstFilename(filename, user);
        file.setName(newFileName);
        try {
            fileRepository.saveAndFlush(file);
        } catch (RuntimeException e) {
            throw new RepositoryException("Ошибка сохранения файла в БД");
        }
        return true;
    }

    public User checkTokenAndGetUserFromDB(String token) {
        return userRepository.findUserByAuthToken(token.substring(7)).orElseThrow(() -> new TokenException("Токен ошибочный"));
    }

    public File getFirstFilename(String filename, User user) {
        return fileRepository.findFirstByNameAndUser(filename, user).orElseThrow(() -> new FileException("Файла не существует"));
    }
}