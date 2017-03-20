package com.example.helloworld;

import com.example.ControllerTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LetterControllerTest extends ControllerTestSupport {
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testTo() throws Exception {
        // Given
        final String who = "MJ";
        final String message = "iloveyou";
        // When
        ResultActions ra = mockMvc.perform(get("/to/{who}", who)
                .param("message", message));
        // Then
        ra.andExpect(status().isOk());
        ra.andExpect(content().string("To MJ iloveyou"));
    }
}