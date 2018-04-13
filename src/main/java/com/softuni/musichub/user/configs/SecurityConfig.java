package com.softuni.musichub.user.configs;

import com.softuni.musichub.user.services.UserExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final int TOKEN_VALIDITY_SECONDS = 864_000;

    private static final String DEV_ENVIRONMENT = "dev";

    private final UserExtractionService userExtractionService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired
    public SecurityConfig(UserExtractionService userExtractionService,
                          BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userExtractionService = userExtractionService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (!this.environment.equals(DEV_ENVIRONMENT)) {
            http.requiresChannel().anyRequest().requiresSecure();
        }

        http.authorizeRequests()
                .antMatchers("/bootstrap-4.0.0/**", "/css/**",
                        "/font-awesome/**", "/images/**",
                        "/jquery/**", "/theme/**", "/scripts/**", "/audiojs/**").permitAll()
                .antMatchers("/users/login", "/users/register").anonymous()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/comments/approve/{id}", "/comments/reject/{id}", "/comments/pending")
                .hasAnyRole("ADMIN", "MODERATOR")
                .antMatchers("/tags/**").hasAnyRole("ADMIN", "MODERATOR")
                .antMatchers("/songs/upload", "/comments/post").authenticated()
                .anyRequest().permitAll()
                .and().exceptionHandling().accessDeniedPage("/")
                .and().formLogin().defaultSuccessUrl("/").loginPage("/users/login")
                .usernameParameter("username").passwordParameter("password")
                .and().rememberMe()
                .rememberMeParameter("rememberMe")
                .tokenValiditySeconds(TOKEN_VALIDITY_SECONDS)
                .and().logout().logoutUrl("/users/logout");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userExtractionService)
                .passwordEncoder(this.passwordEncoder);
    }
}
