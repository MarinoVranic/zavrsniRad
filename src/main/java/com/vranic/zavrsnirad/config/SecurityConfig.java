package com.vranic.zavrsnirad.config;

import com.vranic.zavrsnirad.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.userDetailsService(myUserDetailsService)
                .csrf().disable()
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/appUsers/resetPasswordForm","appUsers/resetPassword").permitAll()
                        .requestMatchers("/CSS/customStyle.css", "/fonts", "/images", "/javascript/sidebar.js", "/javascript/hidedivProvodenjeInventure.js", "/favicon.ico").permitAll()
                        .requestMatchers("/index", "/", "/provodenjeInventure/all", "/provodenjeInventure/addNew", "/provodenjeInventure/scanNew", "/provodenjeInventure/findByGodinaInventure", "/provodenjeInventure/find", "/provodenjeInventure/odaberi_lokaciju", "/appUsers/userResetPassword/", "/appUsers/updatePassword", "appUsers/cancel").access("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
                        .requestMatchers("/inventar/**", "/inventura/**", "/lokacija/**", "/dobavljac/**", "/vrstaUredaja/**", "/racun/**").access("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
                        .requestMatchers("/korisnik/**","/appUsers/**").access("hasRole('SUPER_ADMIN')")
                        .anyRequest().authenticated()
                )
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .failureUrl("/login?error=bad_credentials")
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout")
                .permitAll();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
