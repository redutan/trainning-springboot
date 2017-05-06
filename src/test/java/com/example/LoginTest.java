package com.example;

import com.example.member.Member;
import com.example.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception {
        memberRepository.save(
                new Member("user", passwordEncoder.encode("user"), "USER"));
    }

    @Test
    public void testLogin() throws Exception {
        // given
        // when
        mockMvc.perform(post("/login")
                .with(csrf())
                .param("username", "user")
                .param("password", "user"))
                // then
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "/"));
    }

    @Test
    public void testLogin_NotExistsUsername() throws Exception {
        // given
        // when
        mockMvc.perform(post("/login")
                .with(csrf())
                .param("username", "user0")
                .param("password", "user0"))
                // then
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "/login?error"));
    }

    @Test
    public void testLogin_NotMatchedPassword() throws Exception {
        // given
        // when
        mockMvc.perform(post("/login")
                .with(csrf())
                .param("username", "user")
                .param("password", "user1"))
                // then
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, "/login?error"));
    }
}
