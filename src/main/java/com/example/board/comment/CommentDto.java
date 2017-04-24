package com.example.board.comment;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.hateoas.Link;

/**
 * @author redutan
 * @since 2017. 4. 24.
 */
final class CommentDto {
    private CommentDto() {
        throw new UnsupportedOperationException();
    }

    @Value
    @AllArgsConstructor
    static class Create {
        private String board;
        private String content;
        private String writer;

        Create(Comment comment, Link boardLink) {
            this.board = boardLink.getHref();
            this.content = comment.getContent();
            this.writer = comment.getWriter();
        }
    }
}
