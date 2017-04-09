package com.example.board.comment;

import com.example.board.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/boards/{boardSeq}/comments")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Comment comment,
                                 @PathVariable(value = "boardSeq") Board board,
                                 UriComponentsBuilder uriComponentsBuilder) {
        comment.setBoard(board);
        Comment save = commentRepository.save(comment);
        URI location = getLocation(board, save, uriComponentsBuilder);
        return ResponseEntity.created(location).body(save);
    }

    private URI getLocation(Board board, Comment comment, UriComponentsBuilder uriComponentsBuilder) {
        return uriComponentsBuilder.path("/boards/{boardSeq}/comments/{seq}")
                .buildAndExpand(board.getSeq(), comment.getSeq())
                .toUri();
    }

    @GetMapping
    public List<Comment> list(@PathVariable(value = "boardSeq") Board board) {
        return commentRepository.findByBoard(board);
    }

    @DeleteMapping("/{commentSeq}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "boardSeq") Board board,
                       @PathVariable(value = "commentSeq") Comment comment) {
        commentRepository.delete(comment);
    }
}
