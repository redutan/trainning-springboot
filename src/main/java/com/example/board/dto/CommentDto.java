package com.example.board.dto;

import com.example.board.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.hateoas.Link;

/**
 * @author redutan
 * @since 2017. 4. 24.
 */
public final class CommentDto {
    private CommentDto() {
        throw new UnsupportedOperationException();
    }

    @Value
    @AllArgsConstructor
    public static class Create {
        private String board;
        private String content;
        private String writer;

        public Create(Comment comment, Link boardLink) {
            this.board = boardLink.getHref();
            this.content = comment.getContent();
            this.writer = comment.getWriter();
        }
    }
}
