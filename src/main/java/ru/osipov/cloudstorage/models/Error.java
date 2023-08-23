package ru.osipov.cloudstorage.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {
        private final String message;
        private final int id;
}
