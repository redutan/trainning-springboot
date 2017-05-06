package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @EnableGlobalMethodSecurity(prePostEnabled = true)
    @Configuration
    public static class FormLoginWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
//                    .csrf().disable()       // CSRF를 비활성화 (나중에 자세히)
                    .authorizeRequests()
                    .antMatchers("/admin").hasRole("ADMIN") // 해당 uri에 ADMIN 권한이 있어야 접근 가능
                    .antMatchers("/boards", "/boards/**").hasRole("USER")   // 해당 uri들에 USER 권한이 있어야 접근 가능
                    .antMatchers("/").permitAll()   // "/"는 모두 접근 가능
                    .anyRequest().authenticated()  // 나머지(모든) 요청에 대해서 인증이 요구됨
                    .and()
                    .formLogin()   // Default login uri : "/login"
            ;
            // Default logout uri : "/logout"
        }
    }
}
