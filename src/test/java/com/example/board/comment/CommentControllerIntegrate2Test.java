package com.example.board.comment;

import com.example.board.Board;
import com.example.board.BoardRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@SuppressWarnings("WeakerAccess")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CommentControllerIntegrate2Test {
    private static final ParameterizedTypeReference<Resource<Board>> BOARD_RESOURCE_TYPE = new ParameterizedTypeReference<Resource<Board>>() {
    };
    private static final ParameterizedTypeReference<Resources<Comment>> COMMENT_RESOURCES_TYPE = new ParameterizedTypeReference<Resources<Comment>>() {
    };
    private static final ParameterizedTypeReference<Resource<Comment>> COMMENT_RESOURCE_TYPE = new ParameterizedTypeReference<Resource<Comment>>() {
    };
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    CommentRepository commentRepository;
    // param
    Board board;
    Comment comment;

    @Before
    public void setUp() throws Exception {
        saveBoard();
        comment = random(Comment.class, "seq", "board", "regDate");
//        comment.setBoard(board);
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
        // When
        ResponseEntity<Resource<Comment>> responseEntity =
                restTemplate.exchange("/api/comments", HttpMethod.POST, new HttpEntity<>(create),
                        COMMENT_RESOURCE_TYPE);
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getHeaders().getLocation(), is(notNullValue()));
        assertThat(responseEntity.getBody().getContent(), new ReflectionEquals(comment, "seq", "board", "regDate"));
    }

    private Resource<Board> getBoardResource() {
        ResponseEntity<Resource<Board>> boardResourceResponseEntity =
                restTemplate.exchange("/api/boards/{boardSeq}", HttpMethod.GET, RequestEntity.EMPTY,
                        BOARD_RESOURCE_TYPE,
                        board.getSeq());
        return boardResourceResponseEntity.getBody();
    }

    @Test
    public void testList() throws Exception {
        // Given
        saveComments(11);
        // When
        ResponseEntity<Resources<Comment>> responseEntity =
                restTemplate.exchange("/api/boards/{boardSeq}/comments", HttpMethod.GET, RequestEntity.EMPTY,
                        COMMENT_RESOURCES_TYPE,
                        board.getSeq());
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent(), hasSize(11));
    }

    private List<Comment> saveComments(int size) {
        List<Comment> willSaves = randomListOf(size, Comment.class, "seq", "board");
        for (Comment each : willSaves) {
            each.setBoard(board);
        }
        return commentRepository.save(willSaves);
    }

    @Test
    public void testDelete() throws Exception {
        // Given
        List<Comment> saves = saveComments(1);
        Comment willDelete = saves.get(0);
        // When
        ResponseEntity<Object> responseEntity =
                restTemplate.exchange("/api/comments/{commentSeq}", HttpMethod.DELETE, RequestEntity.EMPTY,
                        Object.class,
                        willDelete.getSeq());
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
        assertThat(commentRepository.exists(willDelete.getSeq()), is(false));
    }
}