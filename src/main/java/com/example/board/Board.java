package com.example.board;

import com.example.board.comment.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.List;

/**
 * 게시물 엔티티
 */
@SuppressWarnings("WeakerAccess")
@Data
@EqualsAndHashCode(exclude = "comments")
@ToString(exclude = "comments")
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
    @DateTimeFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_DATE", nullable = false, updatable = false)
    private Calendar regDate;
    /**
     * 댓글들
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    private List<Comment> comments;

    public static Board withWriter(String writer) {
        Board result = new Board();
        result.setWriter(writer);
        return result;
    }

    public boolean isCreate() {
        //noinspection ConstantConditions
        return seq == null;
    }

    @PrePersist
    void preInsert() {
        this.regDate = Calendar.getInstance();
    }

    public List<Comment> getComments() {
        return comments;
    }
}
