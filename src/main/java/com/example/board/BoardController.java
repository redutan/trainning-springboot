package com.example.board;

import com.example.board.dto.BoardSearch;
import com.example.web.PageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
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
    public String create(@Valid Board board, @AuthenticationPrincipal UserDetails user,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "boards/form";
        }
        board.setWriter(user.getUsername());
        boardRepository.save(board);
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
                       @PageableDefault @SortDefault(value = "seq", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model,
                       HttpServletRequest request) {
        Page<Board> page = boardRepository.findAll(search.toSpec(), pageable);
        model.addAttribute("page", new PageWrapper<>(page, request));
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
