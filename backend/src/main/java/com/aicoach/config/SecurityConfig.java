package com.aicoach.config;

import com.aicoach.auth.service.JwtService;
import com.aicoach.auth.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Stateless security configuration for the API. */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            UserRepository userRepository,
            ObjectMapper objectMapper
    ) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userRepository);

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                writeError(response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Authentication is required", request.getRequestURI()))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeError(response, objectMapper, HttpServletResponse.SC_FORBIDDEN,
                                        "Access denied", request.getRequestURI())))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeError(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            int status,
            String message,
            String path
    ) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(),
                new com.aicoach.auth.exception.ApiError(
                        java.time.Instant.now(), status,
                        status == 401 ? "Unauthorized" : "Forbidden",
                        message, path));
    }
}
