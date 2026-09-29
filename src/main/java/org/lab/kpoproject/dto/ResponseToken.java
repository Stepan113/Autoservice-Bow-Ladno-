package org.lab.kpoproject.dto;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseToken {
    private String accessToken;
    private Cookie cookie;
}
