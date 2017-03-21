package com.example.board;

import lombok.extern.slf4j.Slf4j;
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

import java.net.URI;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class RestBoardControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    // param
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
    public void testCreate() throws Exception {
        // Given
        // When
        ResponseEntity<Object> responseEntity =
                this.restTemplate.postForEntity("/api/boards", board, Object.class);
        log.info("responseEntity = {}", responseEntity);
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED)); // 201
        URI location = responseEntity.getHeaders().getLocation();
        assertThat(location.getPath(), is(startsWith("/api/boards/")));
    }

    @Test
    public void testCreate_TitleIsEmpty() throws Exception {
        // Given
        board.setTitle("");
        // When
        ResponseEntity<Object> responseEntity =
                this.restTemplate.postForEntity("/api/boards", board, Object.class);
        log.info("responseEntity = {}", responseEntity);
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST)); // 400
    }

    @Test
    public void testCreate_ContentIsNull() throws Exception {
        // Given
        board.setContent(null);
        // When
        ResponseEntity<Object> responseEntity =
                this.restTemplate.postForEntity("/api/boards", board, Object.class);
        log.info("responseEntity = {}", responseEntity);
        // Then
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));   // 500
    }
}