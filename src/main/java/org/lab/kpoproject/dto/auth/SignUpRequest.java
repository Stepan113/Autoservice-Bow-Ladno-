package org.lab.kpoproject.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 100,
            message = "Пароль не может быт короче 8" +
                    " символов и длиннее 100 символов")
    private String password;
    @NotBlank(message = "ФИО не может быть пустым")
    private String fio;
}
