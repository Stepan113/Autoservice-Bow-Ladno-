package org.lab.kpoproject.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import org.lab.kpoproject.dto.auth.ResponseToken;
import org.lab.kpoproject.dto.auth.SignInRequest;
import org.lab.kpoproject.dto.auth.SignUpRequest;
import org.lab.kpoproject.entity.TypeToken;
import org.lab.kpoproject.entity.User;
import org.lab.kpoproject.exception.TokenIsntValidException;
import org.lab.kpoproject.exception.TokenNotEqualsException;
import org.lab.kpoproject.mapper.UserMapper;
import org.lab.kpoproject.repository.UserRepository;
import org.lab.kpoproject.utils.jwt.JwtUtils;
import org.lab.kpoproject.utils.redis.RedisUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository rep;
    private final UserMapper mapper;
    private final AuthenticationManager manager;
    private final JwtUtils utils;
    private final RedisUtils redisUtils;

    public UserService(
            final UserRepository rep,
            final UserMapper mapper,
            final AuthenticationManager manager,
            final JwtUtils utils,
            final RedisUtils redisUtils
    ) {
        this.rep = rep;
        this.mapper = mapper;
        this.manager = manager;
        this.utils = utils;
        this.redisUtils = redisUtils;
    }

    public boolean registration(final SignUpRequest signUp) {
        if (rep.findByEmail(signUp.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Пользователь с такой почтой уже есть!");
        }
        final User user = mapper.toEntity(signUp);
        rep.save(user);
        return true;
    }

    public ResponseToken login(final SignInRequest request) {
        final User user = rep.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователя с такой почтой не существует! " +
                                "Зарегистрируйтесь!"));

        final Authentication auth = manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        final String accessToken = utils.generateToken(
                request.getEmail(), user.getRole(), TypeToken.ACCESS);
        final String refreshToken;
        if (redisUtils.exist(request.getEmail())) {
            refreshToken = redisUtils.get(request.getEmail());
        } else {
            refreshToken = utils.generateToken(
                    request.getEmail(), user.getRole(), TypeToken.REFRESH);
            redisUtils.add(request.getEmail(), refreshToken);

        }

        final Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        return new ResponseToken(
                accessToken, cookie);
    }

    public ResponseToken reload(final String refreshToken) {
        final TypeToken typeToken;
        try {
            typeToken = TypeToken.valueOf(utils.getTypeToken(refreshToken));
        } catch (IllegalArgumentException e) {
            throw new TokenIsntValidException("Unknown token type");
        }

        if (typeToken != TypeToken.REFRESH) {
            throw new TokenIsntValidException("Token is not a refresh token");
        }
        final String email = utils.getEmail(refreshToken);
        final String tokenFromRedis = redisUtils.get(email);
        if (tokenFromRedis.equals(refreshToken)) {
            final User user = rep.findByEmail(email).orElseThrow(
                    EntityNotFoundException::new);
            final String newAccessToken = utils.generateToken(
                    email, user.getRole(), TypeToken.ACCESS);
            final ResponseToken response = new ResponseToken();
            response.setAccessToken(newAccessToken);
            final Cookie cookie = new Cookie("refresh_token", refreshToken);
            cookie.setHttpOnly(true);
            response.setCookie(cookie);
            return response;
        }
        throw new TokenNotEqualsException("Refresh token not equals");
    }
}
