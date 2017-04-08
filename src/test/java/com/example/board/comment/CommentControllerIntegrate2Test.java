package com.example.board.comment;

import com.example.board.Board;
import com.example.board.BoardRepository;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CommentControllerIntegrate2Test {
    public static final ParameterizedTypeReference<List<Comment>> LIST_COMMENT_TYPE = new ParameterizedTypeReference<List<Comment>>() {
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
    }

    private void saveBoard() {
        Board willCreate = random(Board.class, "seq", "regDate");
        board = boardRepository.save(willCreate);
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        // When
        ResponseEntity<Object> responseEntity =
                restTemplate.postForEntity("/boards/{boardSeq}/comments", comment, Object.class,
                        board.getSeq());
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(responseEntity.getHeaders().getLocation(), is(notNullValue()));
    }

    @Test
    public void testList() throws Exception {
        // Given
        List<Comment> saves = saveComments(11);
        // When
        ResponseEntity<List<Comment>> responseEntity =
                restTemplate.exchange("/boards/{boardSeq}/comments", HttpMethod.GET, null,
                        LIST_COMMENT_TYPE,
                        board.getSeq());
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), equalTo(saves));
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
        ResponseEntity<Object> responseEntity = restTemplate.exchange("/boards/{boardSeq}/comments/{commentSeq}", HttpMethod.DELETE,
                null, Object.class,
                board.getSeq(), willDelete.getSeq());
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
        assertThat(commentRepository.exists(willDelete.getSeq()), is(false));
    }
}