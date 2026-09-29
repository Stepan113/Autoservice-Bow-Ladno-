package org.lab.kpoproject.utils.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lab.kpoproject.dto.ResponseMessage;
import org.lab.kpoproject.entity.TypeToken;
import org.lab.kpoproject.exception.TokenIsntValidException;
import org.lab.kpoproject.service.UserDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailService service;
    private final JwtUtils utils;

    public JwtFilter(
            final UserDetailService service,
            final JwtUtils utils
    ) {
        this.service = service;
        this.utils = utils;
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String token = getToken(request);
            if (token != null && utils.validateToken(token)) {
                final TypeToken typeToken = TypeToken.valueOf(
                        utils.getTypeToken(token));
                switch (typeToken) {
                    case REFRESH -> throw new TokenIsntValidException(
                            "Refresh token is not a access token");
                    case ACCESS -> {
                        final String email = utils.getEmail(token);
                        final UserDetails user =
                                service.loadUserByUsername(email);
                        final var auth =
                                new UsernamePasswordAuthenticationToken(
                                        user, null, user.getAuthorities()
                                );
                        auth.setDetails(user);
                        SecurityContextHolder.getContext()
                                .setAuthentication(auth);
                    }
                    default -> throw new TokenIsntValidException(
                            "Unknown token type: " + typeToken
                    );
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            final ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Token expired");
            responseMessage.setStatus(HttpStatus.UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(responseMessage));
        }

    }

    private String getToken(final HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
