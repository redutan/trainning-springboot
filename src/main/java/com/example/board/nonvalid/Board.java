package com.example.board.nonvalid;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 게시물 엔티티
 */
@SuppressWarnings("WeakerAccess")
@Data
@Entity(name = "nonvalid.board")
public class Board {
    /**
     * 순번
     */
    @Id
    @GeneratedValue
    private Long seq;
    /**
     * 제목
     */
    private String title;
    /**
     * 내용
     */
    private String content;
    /**
     * 작성자
     */
    private String writer;
    /**
     * 등록일시
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_DATE", insertable = false, updatable = false)
    private Calendar regDate;

    static Board empty() {
        return EMPTY;
    }

    private static final Board EMPTY = new ImmutableBoard();

    private static class ImmutableBoard extends Board {
        @Override
        public void setSeq(Long seq) {
            throw new UnsupportedOperationException("Immutable");
        }

        @Override
        public void setTitle(String title) {
            throw new UnsupportedOperationException("Immutable");
        }

        @Override
        public void setContent(String content) {
            throw new UnsupportedOperationException("Immutable");
        }

        @Override
        public void setWriter(String writer) {
            throw new UnsupportedOperationException("Immutable");
        }

        @Override
        public void setRegDate(Calendar regDate) {
            throw new UnsupportedOperationException("Immutable");
        }
    }
}
