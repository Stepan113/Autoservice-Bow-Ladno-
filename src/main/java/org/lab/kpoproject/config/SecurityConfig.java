package org.lab.kpoproject.config;

import org.lab.kpoproject.Constant;
import org.lab.kpoproject.entity.Role;
import org.lab.kpoproject.utils.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration config;
    private final JwtFilter filter;

    public SecurityConfig(
            final AuthenticationConfiguration config,
            final JwtFilter filter) {
        this.config = config;
        this.filter = filter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/admin/**",
                                        Constant.API + "/admin/**"
                                )
                                .hasRole(Role.ADMIN.name())
                                .requestMatchers(
                                        "/login",
                                        "/registration",
                                        "/reload",
                                        "/test/**",
                                        "/h2-console/**"
                                ).permitAll()
                                .requestMatchers(Constant.API + "/**")
                                .hasAnyRole(Role.ADMIN.name(), Role.USER.name())
                                .anyRequest().authenticated()
                ).logout(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer
                                .FrameOptionsConfig::sameOrigin));
        http.addFilterBefore(
                filter,
                UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
