package org.lab.kpoproject.controller;

import org.lab.kpoproject.Constant;
import org.lab.kpoproject.dto.NewAdminRequest;
import org.lab.kpoproject.service.AdminService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constant.API + "/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(final AdminService service) {
        this.service = service;
    }

    @PostMapping
    public boolean createNewAdmin(
            @RequestBody
            @Validated final NewAdminRequest request) {
        return service.createNewAdmin(request);
    }
}
