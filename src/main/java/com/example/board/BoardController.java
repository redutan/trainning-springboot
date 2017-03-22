package com.example.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * 게시물 컨트롤러
 */
@Controller
@Slf4j
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    /**
     * 게시물 생성 폼
     */
    @GetMapping("/boards/form")
    public String createForm(Model model) {
        model.addAttribute("board", new Board());
        return "boards/form";
    }

    /**
     * 게시물 생성 처리
     */
    @PostMapping("/boards")
    public String create(@Valid Board board, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "boards/form";
        }
        Board save = boardRepository.save(board);
        log.info("save = {}", save);
        // FIXME Redirect /boards
        return "redirect:/boards/form";
    }

    /**
     * 게시물 1건 조회
     */
    @GetMapping("/boards/{seq}")
    public String view(@PathVariable("seq") Board board, Model model) {
        model.addAttribute("board", board);
        return "boards/view";
    }

    @GetMapping("/boards")
    public String list(Model model) {
        Iterable<Board> boards = boardRepository.findAll();
        model.addAttribute("boards", boards);
        return "boards/list";
    }
}
