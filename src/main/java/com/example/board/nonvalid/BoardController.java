package com.example.board.nonvalid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 게시물 컨트롤러
 */
@Controller("nonvalid.BoardController")
@RequestMapping("/nonvalid")
@Slf4j
public class BoardController {

    @Qualifier("nonvalid.boardRepository")
    @Autowired
    private BoardRepository boardRepository;

    /**
     * 게시물 생성 폼
     */
    @GetMapping("/boards/form")
    public String createForm(Model model) {
        return "nonvalid/boards/form";
    }

    /**
     * 게시물 생성 처리
     */
    @PostMapping("/boards")
    public String create(Board board) {
        Board save = boardRepository.save(board);
        log.info("save = {}", save);
        // FIXME Redirect /boards
        return "redirect:/nonvalid/boards/form";
    }
}
