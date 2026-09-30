package org.lab.kpoproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewAdminRequest {
    @NotBlank(message = "Email не должен быть пустым")
    @Email
    private String email;
    @NotBlank(message = "ФИО не должно быть пустым")
    private String fio;
}
