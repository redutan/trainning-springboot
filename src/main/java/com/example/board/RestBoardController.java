package com.example.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/boards")
@Slf4j
public class RestBoardController {
    @Autowired
    private BoardRepository boardRepository;

    /**
     * 게시물 생성 API
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity create(@RequestBody @Valid Board board) {
        Board save = boardRepository.save(board);
        log.info("save = {}", save);
        // Location : Created resource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{seq}")
                .buildAndExpand(save.getSeq()).toUri();
        return ResponseEntity.created(location).build();
    }
}
