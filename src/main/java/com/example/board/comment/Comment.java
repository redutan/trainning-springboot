package com.example.board.comment;

import com.example.board.Board;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "board")
@ToString(exclude = "board")
public class Comment {
    @Id
    @GeneratedValue
    private Long seq;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOARD_SEQ", nullable = false)
    private Board board;
    @Column(name = "CONTENT", nullable = false, length = 4000)
    private String content;
    @Column(name = "WRITER", nullable = false)
    private String writer;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_DATE", nullable = false)
    private Calendar regDate;

    @PrePersist
    void preInsert() {
        regDate = Calendar.getInstance();
    }

    // 복사 생성자
    public Comment(Comment source) {
        BeanUtils.copyProperties(source, this);
    }
}
