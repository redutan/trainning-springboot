package com.example.board.comment;

import com.example.board.Board;
import com.example.board.BoardRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class CommentControllerIntegrate2Test {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    BoardRepository boardRepository;
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
}