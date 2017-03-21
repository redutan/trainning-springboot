package com.example.board.nonvalid;

import com.example.ControllerTestSupport;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Transactional
public class BoardControllerTest extends ControllerTestSupport {
    //param
    private Board board;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    private void createBoard() {
        board = random(Board.class);
        board.setSeq(null);
        board.setRegDate(null);
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        createBoard();
        MultiValueMap<String, String> params = toMultiValueMap();
        // When
        mockMvc.perform(post("/nonvalid/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/nonvalid/boards/form"));
    }

    private MultiValueMap<String, String> toMultiValueMap() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, String> objMap = BeanUtils.describe(board);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(objMap);
        return params;
    }
}