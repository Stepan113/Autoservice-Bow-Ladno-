package org.lab.kpoproject.service;

import org.lab.kpoproject.dto.NewAdminRequest;
import org.lab.kpoproject.entity.Role;
import org.lab.kpoproject.entity.User;
import org.lab.kpoproject.repository.UserRepository;
import org.lab.kpoproject.utils.PasswordGenerator;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository rep;
    private final PasswordGenerator generator;

    public AdminService(final UserRepository rep,
                        final PasswordGenerator generator) {
        this.rep = rep;
        this.generator = generator;
    }

    public boolean createNewAdmin(final NewAdminRequest request) {
        if (rep.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Администратор с такой почтой уже есть!");
        }
        final User admin = new User();
        admin.setEmail(request.getEmail());
        admin.setRole(Role.ADMIN);
        final String password = generator.generate();
        admin.setPassword(generator.encode(password));
        rep.save(admin);
        return true;
    }
}
