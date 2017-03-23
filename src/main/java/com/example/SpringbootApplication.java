package com.example;

import com.example.board.Board;
import com.example.board.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@SpringBootApplication
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

    @Autowired
    private BoardRepository boardRepository;
    
    @PostConstruct
    public void preInitialize() {
        List<Board> boards = IntStream.rangeClosed(1, 10).mapToObj(this::newBoard).collect(toList());
        boardRepository.save(boards);
    }

    private Board newBoard(int i) {
        Board board = new Board();
        board.setTitle("제목입니다. " + i);
        board.setContent("내용입니다. " + i);
        board.setWriter("홍길동" + i);
        return board;
    }
}
