package com.example.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Locale;

/**
 * 게시물 컨트롤러
 */
@Controller
@Slf4j
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Qualifier("messageSource")
    @Autowired
    private MessageSource messageSource;

    /**
     * 게시물 생성 폼
     */
    @GetMapping("/boards/form")

    public String createForm(Model model, Locale locale) {
        log.info("message = {}", messageSource.getMessage("com.example.board.NotEmpty.message", null, locale));
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
}
