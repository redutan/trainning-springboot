package com.example.board.comment;


import com.example.board.Board;
import com.example.board.BoardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Calendar;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.only;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BoardRepository boardRepository;
    @MockBean
    CommentRepository commentRepository;
    // param
    Board board;
    Comment comment;
    Comment saveComment;

    @Before
    public void setUp() throws Exception {
        mockBoard();
    }

    private void mockBoard() {
        board = random(Board.class);
        when(boardRepository.findOne(eq(board.getSeq()))).thenReturn(board);
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        mockingForCreate();
        final String jsonContent = objectMapper.writeValueAsString(comment);
        // When
        mockMvc.perform(post("/boards/{boardSeq}/comments", board.getSeq())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                // Then
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        is(endsWith(String.format("/boards/%s/comments/%s", board.getSeq(), saveComment.getSeq())))));
        verify(commentRepository, only()).save(eq(comment));
    }

    private void mockingForCreate() {
        comment = random(Comment.class, "seq", "board", "regDate");
        when(commentRepository.save(eq(comment))).then(invocation -> {
            saveComment = new Comment(comment);
            saveComment.setSeq(1L);
            saveComment.setBoard(board);
            saveComment.setRegDate(Calendar.getInstance());
            return saveComment;
        });
    }

    @Test
    public void testList() throws Exception {
        // Given
        final int size = 11;
        mockingForComments(size);
        // When
        mockMvc.perform(get("/boards/{boardSeq}/comments", board.getSeq())
                .accept(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(size)));
        verify(commentRepository, only()).findByBoard(eq(board));
    }

    private void mockingForComments(int size) {
        comment = random(Comment.class, "seq", "board", "regDate");
        when(commentRepository.findByBoard(eq(board))).then(invocation -> {
            List<Comment> results = randomListOf(size, Comment.class, "board");
            for (Comment each : results) {
                each.setBoard(board);
            }
            return results;
        });
    }

    @TestConfiguration
    static class TestConfig {
        @Autowired
        private BoardRepository boardRepository;

        @Bean
        WebMvcConfigurer configurer() {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addFormatters(FormatterRegistry registry) {
                    registry.addConverter(String.class, Board.class, id -> boardRepository.findOne(Long.parseLong(id)));
                }
            };
        }
    }
}
