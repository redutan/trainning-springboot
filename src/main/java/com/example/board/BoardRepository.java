package com.example.board;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시물 저장소
 */
@SuppressWarnings("WeakerAccess")
@Repository
public interface BoardRepository extends CrudRepository<Board, Long> {
    List<Board> findByTitleContaining(String title);

    List<Board> findByWriter(String writer);
}
