package org.lab.kpoproject.mapper;

import org.lab.kpoproject.dto.auth.SignInRequest;
import org.lab.kpoproject.dto.auth.SignUpRequest;
import org.lab.kpoproject.dto.UserResponse;
import org.lab.kpoproject.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder encoder;

    protected abstract User toEntityInternal(SignUpRequest request);

    public User toEntity(final SignUpRequest request) {
        final User user = toEntityInternal(request);
        user.setPassword(encoder.encode(request.getPassword()));
        return user;
    }

    public abstract User toEntity(SignInRequest request);

    public abstract UserResponse toDto(User user);
}
