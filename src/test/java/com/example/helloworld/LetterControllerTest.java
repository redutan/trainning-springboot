package com.example.helloworld;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LetterController.class)
public class LetterControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testTo() throws Exception {
        // Given
        final String who = "MJ";
        final String message = "iloveyou";
        // When
        mvc.perform(get("/to/{who}", who)
                .param("message", message))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().string("To MJ iloveyou"));
    }
}