package com.alten.controllers.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SecurityDataInDto {
    @NotBlank(message = "username should be provided")
    private String username;
    @NotBlank(message = "password should be provided")
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
