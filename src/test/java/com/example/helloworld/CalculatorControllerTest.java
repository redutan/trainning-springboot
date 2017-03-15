package com.example.helloworld;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CalculatorControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void testAdder_12345() throws Exception {
        // Given
        final List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        ResultActions ra = mockMvc.perform(get("/calculator/adder/{numbers}", numbersString));
        // Then
        ra.andExpect(status().isOk());
        ra.andExpect(content().string("10"));
    }

    @Test
    public void testAdder_None() throws Exception {
        // Given
        // When
        ResultActions ra = mockMvc.perform(get("/calculator/adder/"));
        // Then
        ra.andExpect(status().isNotFound());
    }

    @Test
    public void testAdder_Duplicated() throws Exception {
        // Given
        final List<Integer> numbers = Arrays.asList(1, 2, 2);
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        ResultActions ra = mockMvc.perform(get("/calculator/adder/{numbers}", numbersString));
        // Then
        ra.andExpect(content().string("오류 : 같은 수는 입력할 수 없습니다."));
        ra.andExpect(status().isBadRequest());
    }
}