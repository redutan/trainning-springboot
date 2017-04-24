package com.example.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
