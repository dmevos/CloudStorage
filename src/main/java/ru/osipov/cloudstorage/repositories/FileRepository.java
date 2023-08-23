package ru.osipov.cloudstorage.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.osipov.cloudstorage.entities.File;
import ru.osipov.cloudstorage.entities.User;


import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findAllFilesByUser(User user, Pageable pageable);
    Optional<File> findFirstByNameAndUser(String filename, User user);
}