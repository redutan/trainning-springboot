package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

    @Configuration
    public static class FormLoginWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()       // CSRF를 비활성화 (나중에 자세히)
                    .authorizeRequests()
                    .antMatchers("/").permitAll()   // "/"는 모두 접근 가능
                    .anyRequest().authenticated()  // 나머지(모든) 요청에 대해서 인증이 요구됨
                    .and()
                    .formLogin()   // 폼 로그인 : "/login"
                    .and()
                    .logout()   // 기본 : "/logout"
            ;
        }
    }
}
