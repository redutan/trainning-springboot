package com.example.board;

/**
 * 게시물을 찾을 수 없는 경우 발생
 */
public class BoardNotFoundException extends RuntimeException {
    public BoardNotFoundException(String message) {
        super(message);
    }
}