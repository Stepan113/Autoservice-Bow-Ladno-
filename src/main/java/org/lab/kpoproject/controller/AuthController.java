package org.lab.kpoproject.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.lab.kpoproject.dto.ResponseToken;
import org.lab.kpoproject.dto.SignInRequest;
import org.lab.kpoproject.dto.SignUpRequest;
import org.lab.kpoproject.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserService service;

    public AuthController(final UserService service) {
        this.service = service;
    }

    @PostMapping("/registration")
    public boolean registration(@Validated final SignUpRequest request) {
        return service.registration(request);
    }

    @PostMapping("/login")
    public ResponseToken login(@Validated final SignInRequest request,
                               final HttpServletResponse response) {
        final ResponseToken token = service.login(request);
        response.addCookie(token.getCookie());
        return token;
    }

    @PostMapping("/reload")
    public ResponseToken reload(
            @CookieValue("refresh_token") final String refreshToken,
            final HttpServletResponse response) {
        final ResponseToken token = service
                .reload(refreshToken);
        response.addCookie(token.getCookie());
        return token;
    }

}
