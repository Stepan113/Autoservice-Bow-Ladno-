package org.lab.kpoproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequest {
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
