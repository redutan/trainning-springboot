package com.example.board;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.NestedServletException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private BoardRepository boardRepository;
    //param
    private Board board;

    @Before
    public void setUp() throws Exception {
        createBoard();
    }

    private void createBoard() {
        board = random(Board.class);
        board.setSeq(null);
        board.setRegDate(null);
    }

    @Test
    public void testCreateForm() throws Exception {
        mvc.perform(get("/boards/form")
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())   // 200
                .andExpect(view().name("boards/form"))
                .andExpect(content().string(containsString("게시물 등록")));
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/boards/form"));
    }

    private MultiValueMap<String, String> toMultiValueMap(Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, String> objMap = BeanUtils.describe(obj);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(objMap);
        return params;
    }

    @Test
    public void testCreate_TitleIsEmpty() throws Exception {
        // Given
        board.setTitle("");
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
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
        board.setTitle(null);
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
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
        board.setWriter("");
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/boards/form"));
    }

    @Test
    public void testCreate_WriterIsNull() throws Exception {
        // Given
        board.setWriter(null);
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeHasFieldErrorCode("board", "writer", is("NotNull")));
    }

    @Test(expected = NestedServletException.class)
    public void testCreate_ContentIsNull() throws Exception {
        // Given
        board.setContent(null);
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isInternalServerError());   // 500
    }

    @Test
    public void testView() throws Exception {
        // Given
        final Board saved = boardRepository.save(board);
        // When
        mvc.perform(get("/boards/{seq}", saved.getSeq())
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/view"))
                .andExpect(model().attribute("board", is(saved)));
    }
}
