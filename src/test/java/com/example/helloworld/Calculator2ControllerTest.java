package com.example.helloworld;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class Calculator2ControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testAdder_1234() throws Exception {
        // Given
        final List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        mvc.perform(get("/calculator2/adder/{numbers}", numbersString))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(10)));
    }

    @Test
    public void testAdder_12345() throws Exception {
        // Given
        final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        mvc.perform(get("/calculator2/adder/{numbers}", numbersString))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(15)));
    }

    @Test
    public void testAdder_1() throws Exception {
        // Given
        final Integer number = 1;
        // When
        ResultActions ra = mvc.perform(get("/calculator2/adder/{numbers}", number))
                // Then
                .andExpect(content().string("오류 : 숫자는 최소 2개 이상 입력해야합니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdder_111() throws Exception {
        // Given
        final List<Integer> numbers = Arrays.asList(1, 1, 1);
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        mvc.perform(get("/calculator2/adder/{numbers}", numbersString))
                // Then
                .andExpect(content().string("오류 : 중복되는 수가 존재합니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdder_121() throws Exception {
        // Given
        final List<Integer> numbers = Arrays.asList(1, 2, 1);
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        mvc.perform(get("/calculator2/adder/{numbers}", numbersString))
                // Then
                .andExpect(content().string("오류 : 중복되는 수가 존재합니다."))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdder_None() throws Exception {
        // Given
        // When
        ResultActions ra = mvc.perform(get("/calculator2/adder/"))
                // Then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAccumulator_1() throws Exception {
        // Given
        final Integer number = 1;
        // When
        ResultActions ra = mvc.perform(get("/calculator2/accumulator/{numbers}", number));
        // Then
        ra.andExpect(status().isBadRequest());
        ra.andExpect(content().string("오류 : 숫자는 최소 2개 이상 입력해야합니다."));
    }

    @Test
    public void testAccumulator_212() throws Exception {
        // Given
        final List<Integer> numbers = Arrays.asList(2, 1, 2);
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        ResultActions ra = mvc.perform(get("/calculator2/accumulator/{numbers}", numbersString));
        // Then
        ra.andExpect(content().string("오류 : 중복되는 수가 존재합니다."));
        ra.andExpect(status().isBadRequest());
    }

    @Test
    public void testAccumulator() throws Exception {
        testAccumulator(Arrays.asList(1, 2, 3, 4), 10);
        testAccumulator(Arrays.asList(1, 2, 3, 4, 5), 25);
    }

    private void testAccumulator(List<Integer> numbers, int actual) throws Exception {
        // Given
        final String numbersString = numbers.stream().map(String::valueOf).collect(joining(","));
        // When
        mvc.perform(get("/calculator2/accumulator/{numbers}", numbersString))
                // Then
                .andExpect(status().isOk()) // 200
                .andExpect(content().string(String.valueOf(actual)));
    }
}