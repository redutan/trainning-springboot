package com.example.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

//@Repository
@PreAuthorize("hasRole('USER')")
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Override
    @PreAuthorize("@commentRepository.findOne(#id)?.writer == authentication?.name")
    void delete(Long id);

    @Override
    @PreAuthorize("#comment?.writer == authentication?.name")
    void delete(Comment comment);
}
