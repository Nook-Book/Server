package com.nookbook.global.config.security;


import com.nookbook.domain.auth.application.CustomDefaultOAuth2UserService;
import com.nookbook.domain.auth.application.CustomUserDetailsService;
import com.nookbook.domain.auth.domain.repository.CustomAuthorizationRequestRepository;
import com.nookbook.global.config.security.handler.CustomSimpleUrlAuthenticationFailureHandler;
import com.nookbook.global.config.security.handler.CustomSimpleUrlAuthenticationSuccessHandler;
import com.nookbook.global.config.security.token.CustomOncePerRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);


     private final CustomUserDetailsService customUserDetailsService;
     private final CustomDefaultOAuth2UserService customOAuth2UserService;
     private final CustomSimpleUrlAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
     private final CustomSimpleUrlAuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
     private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomOncePerRequestFilter customOncePerRequestFilter() {
        return new CustomOncePerRequestFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // OAuth2 관련 의존성 제거
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // .exceptionHandling(exception -> exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/error", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js")
                        .permitAll()
                        .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/login/**", "/auth/idTokenLogin", "/oauth2/**", "/api/v1/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                        .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)));

        http.addFilterBefore(customOncePerRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}