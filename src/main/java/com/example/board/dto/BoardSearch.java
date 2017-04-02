package com.example.board.dto;

import com.example.board.Board;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BoardSearch {
    private String title;
    private String writer;

    public Specification<Board> toSpec() {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!StringUtils.isEmpty(title)) {
                predicates.add(builder.like(root.get("title"), "%" + title + "%"));
            }
            if (!StringUtils.isEmpty(writer)) {
                predicates.add(builder.equal(root.get("writer"), writer));
            }
            return builder.and(predicates.stream().toArray(Predicate[]::new));
        };
    }
}
