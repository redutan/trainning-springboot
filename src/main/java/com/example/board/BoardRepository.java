package com.example.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시물 저장소
 */
@SuppressWarnings("WeakerAccess")
@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    List<Board> findByTitleContaining(String title);

    List<Board> findByWriter(String writer);
}
