package ru.osipov.cloudstorage.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {
    private String authToken;

    @JsonProperty("auth-token")
    public String getAuthToken() {
        return authToken;
    }
}