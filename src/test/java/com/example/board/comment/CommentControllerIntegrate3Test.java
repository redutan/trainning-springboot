package com.example.board.comment;

import com.example.board.Board;
import com.example.board.BoardRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CommentControllerIntegrate3Test {
    @LocalServerPort
    int port;
    @Autowired
    BoardRepository boardRepository;
    // param
    Board board;
    Comment comment;

    @Before
    public void setUp() throws Exception {
        saveBoard();
        comment = random(Comment.class, "seq", "board", "regDate");

        RestAssured.port = port;
    }

    private void saveBoard() {
        Board willCreate = random(Board.class, "seq", "regDate");
        board = boardRepository.saveAndFlush(willCreate);
    }

    @Transactional
    @Test
    public void testCreate() throws Exception {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(comment)
        .when()
                .post("/boards/{boardSeq}/comments", board.getSeq())
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .header(HttpHeaders.LOCATION, is(notNullValue()));
    }
}