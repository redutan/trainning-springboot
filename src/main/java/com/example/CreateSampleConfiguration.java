package com.example;

import com.example.board.Board;
import com.example.board.BoardRepository;
import com.example.board.comment.Comment;
import com.example.board.comment.CommentRepository;
import com.example.member.Member;
import com.example.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Configuration
@ConditionalOnMissingClass("com.example.SpringbootApplicationTests")
public class CreateSampleConfiguration {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void preInitialize() {
        List<Board> boards = IntStream.rangeClosed(1, 25).mapToObj(this::newBoard).collect(toList());
        List<Board> saveBoards = boardRepository.save(boards);
        Board board = saveBoards.get(0);

        List<Comment> comments = IntStream.rangeClosed(1, 5).mapToObj(i -> newComment(board, i)).collect(toList());
        commentRepository.save(comments);
    }

    private Board newBoard(int i) {
        Board board = new Board();
        board.setTitle("제목입니다. " + i);
        board.setContent("내용입니다. " + i);
        board.setWriter("홍길동" + i);
        return board;
    }

    private Comment newComment(Board board, int i) {
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setContent("댓글입니다." + i);
        comment.setWriter("홍길동" + i);
        return comment;
    }

    @PostConstruct
    public void createMembers() {
        List<Member> members = Arrays.asList(
                new Member("user", passwordEncoder.encode("user"), "USER"),
                new Member("admin", passwordEncoder.encode("admin"), "ADMIN"));
        memberRepository.save(members);
    }
}
