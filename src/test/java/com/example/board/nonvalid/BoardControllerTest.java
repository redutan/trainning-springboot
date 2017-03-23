package com.example.board.nonvalid;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BoardControllerTest {
    @Autowired
    private MockMvc mvc;
    //param
    private Board board;

    @Before
    public void setUp() throws Exception {
        createBoard();
    }

    private void createBoard() {
//        board = random(Board.class, "seq", "regDate");
        board = random(Board.class);
        board.setSeq(null);
        board.setRegDate(null);
    }

    @Test
    public void testCreateForm() throws Exception {
        mvc.perform(get("/nonvalid/boards/form")
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())   // 200
                .andExpect(view().name("boards/form"))
                .andExpect(content().string(containsString("게시물 저장")));
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/nonvalid/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/nonvalid/boards/form"));
    }

    private MultiValueMap<String, String> toMultiValueMap(Object obj) throws Exception {
        Map<String, String> objMap = BeanUtils.describe(obj);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(objMap);
        return params;
    }
}