package org.lab.kpoproject.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SignUpRequest {
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;
    private String password;
    private String fio;
}
