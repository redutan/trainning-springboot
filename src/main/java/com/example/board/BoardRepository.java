package com.example.board;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * 게시물 저장소
 */
@SuppressWarnings("WeakerAccess")
@Repository
public interface BoardRepository extends CrudRepository<Board, Long> {
}
