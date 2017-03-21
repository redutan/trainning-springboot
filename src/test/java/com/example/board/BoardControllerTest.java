package com.example.board;

import com.example.ControllerTestSupport;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        ResultActions ra = mockMvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/boards/form"));
    }

    private MultiValueMap<String, String> toMultiValueMap() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, String> objMap = BeanUtils.describe(board);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(objMap);
        return params;
    }

    @Test
    public void testCreate_TitleIsEmpty() throws Exception {
        // Given
        createBoard();
        board.setTitle("");
        MultiValueMap<String, String> params = toMultiValueMap();
        // When
        ResultActions ra = mockMvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeHasFieldErrorCode("board", "title", is("NotEmpty")));
    }

    @Test
    public void testCreate_TitleIsNull() throws Exception {
        // Given
        createBoard();
        board.setTitle(null);
        MultiValueMap<String, String> params = toMultiValueMap();
        // When
        ResultActions ra = mockMvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeHasFieldErrorCode("board", "title", is("NotEmpty")));
    }

    @Test
    public void testCreate_WriterIsEmpty() throws Exception {
        // Given
        createBoard();
        board.setWriter("");
        MultiValueMap<String, String> params = toMultiValueMap();
        // When
        ResultActions ra = mockMvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/boards/form"));
    }

    @Test
    public void testCreate_WriterIsNull() throws Exception {
        // Given
        createBoard();
        board.setWriter(null);
        MultiValueMap<String, String> params = toMultiValueMap();
        // When
        ResultActions ra = mockMvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeHasFieldErrorCode("board", "writer", is("NotNull")));
    }
}
