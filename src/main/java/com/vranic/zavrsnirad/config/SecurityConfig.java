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
//                .authorizeRequests()
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/CSS/customStyle.css", "/fonts", "/images", "/javascript/sidebar.js", "/javascript/hidedivProvodenjeInventure.js").permitAll()
                        .requestMatchers("/index", "/", "/provodenjeInventure/all", "/provodenjeInventure/addNew", "/provodenjeInventure/scanNew", "/provodenjeInventure/findByGodinaInventure", "/provodenjeInventure/find", "/provodenjeInventure/odaberi_lokaciju").access("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
                        .requestMatchers("/korisnik/**", "/inventar/**", "/inventura/**", "/lokacija/**", "/dobavljac/**", "/vrstaUredaja/**", "/racun/**").access("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
                        .requestMatchers("/appUsers/**").access("hasRole('SUPER_ADMIN')")
                        .anyRequest().authenticated()
                )
//                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // Allow access to static resources
//                .requestMatchers("/CSS/customStyle.css", "/fonts", "/images", "/javascript/sidebar.js", "/javascript/hidedivProvodenjeInventure.js").permitAll()
//                .requestMatchers("/index","/","/provodenjeInventure/all", "/provodenjeInventure/addNew", "/provodenjeInventure/scanNew", "/provodenjeInventure/findByGodinaInventure", "provodenjeInventure/find", "provodenjeInventure/odaberi_lokaciju").hasAnyRole("SUPER_ADMIN", "ADMIN", "USER")
//                .requestMatchers("/korisnik/**", "inventar/**", "inventura/**", "lokacija/**", "dobavljac/**", "vrstaUredaja/**", "racun/**", "appUsers/**").hasRole("SUPER_ADMIN")
//                .requestMatchers("/korisnik/**", "inventar/**", "inventura/**", "lokacija/**", "dobavljac/**", "vrstaUredaja/**", "racun/**").hasRole("ADMIN")
//                .anyRequest()
//                .authenticated()
//                .and()
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
