package com.example.board.dto;

import com.example.board.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시물 검색
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearch {
    private static final Predicate[] EMPTY_PREDICATES = new Predicate[0];
    /**
     * 제목 검색
     */
    private String title;
    /**
     * 작성자 검색
     */
    private String writer;

    public Specification<Board> toSpec() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 제목 contains 검색
            if (!StringUtils.isEmpty(title)) {
                predicates.add(builder.like(root.get("title"), "%" + title + "%"));
            }
            // 작성자 equal 검색
            if (!StringUtils.isEmpty(writer)) {
                predicates.add(builder.equal(root.get("writer"), writer));
            }
            return builder.and(predicates.toArray(EMPTY_PREDICATES));
        };
    }
}
