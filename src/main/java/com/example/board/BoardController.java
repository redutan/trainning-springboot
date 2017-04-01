package com.example.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        return "redirect:/boards";
    }

    /**
     * 게시물 1건 조회
     */
    @GetMapping("/boards/{seq}")
    public String view(@PathVariable("seq") Board board, Model model) {
        model.addAttribute("board", board);
        return "boards/view";
    }

    /**
     * 게시물 목록 조회
     */
    @GetMapping("/boards")
    public String list(@RequestParam(required = false) String title,
                       @RequestParam(required = false) String writer,
                       Model model) {
        Iterable<Board> boards;
        if (title != null) {    // 제목 검색
            boards = boardRepository.findByTitleContaining(title);
            model.addAttribute("title", title);
        } else if (writer != null) {    // 작성자 검색
            boards = boardRepository.findByWriter(writer);
            model.addAttribute("writer", writer);
        } else {
            boards = boardRepository.findAll();
        }
        model.addAttribute("boards", boards);
        return "boards/list";
    }

    /**
     * 게시물 1건 삭제
     */
    @RequestMapping("/boards/{seq}/delete")
    public String delete(@PathVariable("seq") Long seq) {
        boardRepository.delete(seq);
        return "redirect:/boards";
    }

    /**
     * 게시물 수정 폼
     */
    @GetMapping("/boards/{seq}/form")
    public String updateForm(@PathVariable("seq") Board board, Model model) {
        model.addAttribute("board", board);
        return "boards/form";
    }
}
