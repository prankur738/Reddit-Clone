package io.mountblue.redditclone.security;

import io.mountblue.redditclone.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();

        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());

        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authConfig ->
                        authConfig
                                .requestMatchers("/", "signup/**", "/css/**").permitAll()

                                .requestMatchers("/", "signup/**", "/css/**", "/{subredditName}/posts/{postId}/comments/*").permitAll()
                                .anyRequest().authenticated()
                ).formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateUser")
                                .permitAll()
                ).logout(logout ->
                    logout
                            .logoutSuccessUrl("/")
                            .permitAll()
                ).exceptionHandling(exceptionConfig ->
                        exceptionConfig
                                .accessDeniedPage("/access-denied")
                )
                .build();
    }
}
