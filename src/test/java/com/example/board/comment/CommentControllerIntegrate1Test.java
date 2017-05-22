package com.example.board.comment;

import com.example.board.Board;
import com.example.board.BoardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ResolvableType;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

import static com.example.board.comment.CommentControllerIntegrate1Test.USERNAME;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = USERNAME, roles = "USER")
@Slf4j
public class CommentControllerIntegrate1Test {
    public static final String USERNAME = "user";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Qualifier("halJacksonHttpMessageConverter")
    @Autowired
    private TypeConstrainedMappingJackson2HttpMessageConverter halJacksonHttpMessageConverter;

    // param
    private Board board;
    private Comment comment;

    @Before
    public void setUp() throws Exception {
        saveBoard();
        comment = random(Comment.class, "seq", "board", "regDate");
    }

    private void saveBoard() {
        Board willCreate = random(Board.class, "seq", "regDate");
        board = boardRepository.save(willCreate);
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        Resource<Board> boardResource = getBoardResource();
        CommentDto.Create create = new CommentDto.Create(comment, boardResource.getId());
        final String jsonContent = objectMapper.writeValueAsString(create);
        // When
        mockMvc.perform(post("/api/comments")
                .with(csrf())
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaTypes.HAL_JSON)
                .content(jsonContent))
                // Then
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, is(notNullValue())));
    }

    private Resource<Board> getBoardResource() throws Exception {
        return getHalResource(() -> get("/api/boards/{boardSeq}", board.getSeq()), Board.class);
    }

    private <T> Resource<T> getHalResource(Supplier<MockHttpServletRequestBuilder> requestBuilderSupplier,
                                           Class<T> responseClass) throws Exception {
        MvcResult mvcResult = mockMvc.perform(requestBuilderSupplier.get()
                .with(csrf())
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaTypes.HAL_JSON))
                .andReturn();
        return getHalResource(mvcResult, responseClass);
    }

    @SuppressWarnings("unchecked")
    private <T> Resource<T> getHalResource(MvcResult mvcResult, Class<T> responseClass) throws IOException {
        return (Resource<T>) halJacksonHttpMessageConverter.read(
                ResolvableType.forClassWithGenerics(Resource.class, responseClass).getType(),
                null,
                new MockHttpInputMessage(mvcResult.getResponse().getContentAsByteArray()));
    }

    @Test
    public void testList() throws Exception {
        // Given
        saveComments(11);
        // When
        mockMvc.perform(get("/api/comments")
                .with(csrf())
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaTypes.HAL_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.comments", hasSize(11)));
    }

    private List<Comment> saveComments(int size) {
        return saveComments(size, null);
    }

    private List<Comment> saveComments(int size, Supplier<String> writerSupplier) {
        List<Comment> willSaves = randomListOf(size, Comment.class, "seq", "board");
        for (Comment each : willSaves) {
            each.setBoard(board);
            if (writerSupplier != null) {
                each.setWriter(writerSupplier.get());
            }
        }
        return commentRepository.save(willSaves);
    }

    @Test
    public void testDelete_OtherUser() throws Exception {
        // Given
        List<Comment> saves = saveComments(1);
        Comment willDelete = saves.get(0);
        // When
        mockMvc.perform(delete("/api/comments/{commentSeq}", willDelete.getSeq())
                .with(csrf())
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaTypes.HAL_JSON))
                // Then
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDelete() throws Exception {
        // Given
        List<Comment> saves = saveMyComments(1);
        Comment willDelete = saves.get(0);
        // When
        mockMvc.perform(delete("/api/comments/{commentSeq}", willDelete.getSeq())
                .with(csrf())
                .accept(MediaTypes.HAL_JSON)
                .contentType(MediaTypes.HAL_JSON))
                // Then
                .andExpect(status().isNoContent());
    }

    private List<Comment> saveMyComments(@SuppressWarnings("SameParameterValue") int size) {
        return saveComments(size, () -> USERNAME);
    }
}