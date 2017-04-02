package com.example.board;

import com.example.board.dto.BoardSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

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

//    /**
//     * 게시물 목록 조회
//     */
//    @GetMapping("/boards")
//    public String list(@RequestParam(required = false) String title,
//                       @RequestParam(required = false) String writer,
//                       Model model) {
//        Board board = new Board();
//        board.setTitle(title);
//        board.setWriter(writer);
//        Iterable<Board> boards = boardRepository.findAll(Example.of(board, matching()
//                .withIgnoreNullValues()
//                .withMatcher("title", matcher -> matcher.contains())
//                .withMatcher("writer", matcher -> matcher.storeDefaultMatching())));
//        model.addAttribute("boards", boards);
//        model.addAttribute("title", title);
//        model.addAttribute("writer", writer);
//        return "boards/list";
//    }

    /**
     * 게시물 목록 조회
     */
    @GetMapping("/boards")
    public String list(BoardSearch search,
                       Model model) {
        List<Board> boards = boardRepository.findAll(search.toSpec());
        model.addAttribute("boards", boards);
        model.addAttribute("search", search);
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
