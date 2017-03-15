package com.example.board;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BoardControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    //param
    private Board board1;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .alwaysDo(print())
                .build();
        board1 = random(Board.class);
        board1.setSeq(null);
        board1.setRegDate(null);
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        Map<String, String> objMap = BeanUtils.describe(board1);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(objMap);
        // When
        ResultActions ra = mockMvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params));
        // Then
        ra.andExpect(status().isFound());   // 302
        ra.andExpect(view().name("redirect:/boards/form"));
    }
}
