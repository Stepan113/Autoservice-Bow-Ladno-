package org.lab.kpoproject.controller;

import org.lab.kpoproject.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final EmailService service;

    public TestController(final EmailService service) {
        this.service = service;
    }

    @GetMapping("/email")
    public void sendEmail() {
        service.sendNewAdminCredentials(
                "CtepanCecevitcin@yandex.ru",
                "Абракадабра",
                "123456789");
    }
}
