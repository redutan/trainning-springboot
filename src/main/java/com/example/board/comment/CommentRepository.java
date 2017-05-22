package com.example.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

//@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    @PreAuthorize("hasRole('USER') and @commentRepository.findOne(#id)?.writer == authentication?.name")
    void delete(Long id);

    @Override
    @PreAuthorize("hasRole('USER') and #comment?.writer == authentication?.name")
    void delete(Comment comment);
}
