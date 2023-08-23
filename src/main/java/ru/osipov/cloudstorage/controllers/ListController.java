package ru.osipov.cloudstorage.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.osipov.cloudstorage.dto.FileDTO;
import ru.osipov.cloudstorage.services.FileService;

import java.util.List;

@RestController
@RequestMapping("/list")
@AllArgsConstructor
public class ListController {

    private final FileService fileService;

    @GetMapping
    public List<FileDTO> getList(@RequestHeader("auth-token") String authToken, @RequestParam("limit") Integer limit) {
        return fileService.getList(authToken, limit);
    }
}
