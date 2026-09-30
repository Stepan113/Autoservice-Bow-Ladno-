package org.lab.kpoproject.service;

import org.lab.kpoproject.dto.NewAdminRequest;
import org.lab.kpoproject.entity.Role;
import org.lab.kpoproject.entity.User;
import org.lab.kpoproject.repository.UserRepository;
import org.lab.kpoproject.utils.PasswordGenerator;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository rep;
    private final PasswordGenerator generator;
    private final EmailService emailService;

    public AdminService(final UserRepository rep,
                        final PasswordGenerator generator,
                        final EmailService emailService) {
        this.rep = rep;
        this.generator = generator;
        this.emailService = emailService;
    }

    public boolean createNewAdmin(final NewAdminRequest request) {
        if (rep.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Администратор с такой почтой уже есть!");
        }
        final User admin = new User();
        admin.setEmail(request.getEmail());
        admin.setFio(request.getFio());
        admin.setRole(Role.ADMIN);
        final String password = generator.generate();
        admin.setPassword(generator.encode(password));
        final User savedAdmin = rep.save(admin);
        try {
            emailService.sendNewAdminCredentials(
                    savedAdmin.getEmail(),
                    savedAdmin.getFio(),
                    password
            );
        } catch (MailException e) {
            rep.delete(savedAdmin);
            throw e;
        }
        return true;
    }
}
