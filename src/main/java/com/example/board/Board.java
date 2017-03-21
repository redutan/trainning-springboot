package com.example.board;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 게시물 엔티티
 */
@SuppressWarnings("WeakerAccess")
@Data
@Entity
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
    @NotEmpty
    @Column(nullable = false)
    private String title;
    /**
     * 내용
     */
    @Lob
    @Column(nullable = false)
    private String content;
    /**
     * 작성자
     */
    @NotNull
    @Column(nullable = false)
    private String writer;
    /**
     * 등록일시
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "REG_DATE", insertable = false, updatable = false)
    private Date regDate;

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
        public void setRegDate(Date regDate) {
            throw new UnsupportedOperationException("Immutable");
        }
    }
}
