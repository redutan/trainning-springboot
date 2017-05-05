package com.example.board;

import com.example.board.dto.BoardSearch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.board.BoardControllerTest.USERNAME;
import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
@WithMockUser(username = USERNAME, roles = "USER")
public class BoardControllerTest {
    public static final String USERNAME = "user1";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private BoardRepository boardRepository;
    //param
    private Board board;

    @Before
    public void setUp() throws Exception {
        createBoard();
    }

    private void createBoard() {
        board = random(Board.class);
        board.setSeq(null);
        board.setRegDate(null);
        board.setComments(null);
    }

    @Test
    public void testCreateForm() throws Exception {
        mvc.perform(get("/boards/form")
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())   // 200
                .andExpect(view().name("boards/form"))
                .andExpect(content().string(containsString("게시물 저장")))
                .andExpect(content().string(containsString(USERNAME)));
    }

    @Test
    public void testCreate() throws Exception {
        // Given
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/boards"));
        Board createBoard = getLastCreateBoard();
        board.setWriter(USERNAME);
        assertBoard(createBoard, board);
    }

    private MultiValueMap<String, String> toMultiValueMap(Object obj) throws Exception {
        Map<String, String> objMap = BeanUtils.describe(obj);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(objMap);
        return params;
    }

    private Board getLastCreateBoard() {
        Page<Board> page = boardRepository.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "seq"));
        return page.getContent().get(0);
    }

    private void assertBoard(Board expected, Board actual) {
        assertThat(expected.getTitle(), is(actual.getTitle()));
        assertThat(expected.getContent(), is(actual.getContent()));
        assertThat(expected.getWriter(), is(actual.getWriter()));
    }

    @Test
    public void testCreate_TitleIsEmpty() throws Exception {
        // Given
        board.setTitle("");
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeHasFieldErrorCode("board", "title", is("NotEmpty")));
    }

    @Test
    public void testCreate_TitleIsNull() throws Exception {
        // Given
        board.setTitle(null);
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeHasFieldErrorCode("board", "title", is("NotEmpty")));
    }

    @Test
    @Ignore
    public void testCreate_WriterIsEmpty() throws Exception {
        // Given
        board.setWriter("");
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/boards"));
    }

    @Test
    public void testCreate_WriterIsNull() throws Exception {
        // Given
        board.setWriter(null);
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeHasFieldErrorCode("board", "writer", is("NotNull")));
    }

    @Test
    @Ignore
    public void testCreate_ContentIsNull() throws Exception {
        // Given
        board.setContent(null);
        MultiValueMap<String, String> params = toMultiValueMap(board);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isInternalServerError());   // 500
    }

    @Test
    public void testView() throws Exception {
        // Given
        final Board saved = boardRepository.save(board);
        // When
        mvc.perform(get("/boards/{seq}", saved.getSeq())
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/view"))
                .andExpect(model().attribute("board", is(saved)));
    }

    @Test
    @Ignore
    public void testList() throws Exception {
        // Given
        final int count = 10;
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        Iterable<Board> saveds = boardRepository.save(boards);
        // When
        mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attribute("boards", saveds))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void testDelete() throws Exception {
        // Given
        final int count = 2;
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate", "writer");
        for (Board each : boards) {
            each.setWriter(USERNAME);
        }
        Iterable<Board> saveds = boardRepository.save(boards);
        final Board first = saveds.iterator().next();
        // When
        mvc.perform(get("/boards/{seq}/delete", first.getSeq())
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isFound())  // 302
                .andExpect(view().name("redirect:/boards"));
        assertThat(boardRepository.exists(first.getSeq()), is(false));  // 삭제되었는가?
        assertThat(boardRepository.count(), is(count - 1L)); // 하나만 삭제되었는가?
    }

    @Test
    public void testDelete_OtherUser() throws Exception {
        // Given
        final int count = 2;
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        Iterable<Board> saveds = boardRepository.save(boards);
        final Board first = saveds.iterator().next();
        // When
        mvc.perform(get("/boards/{seq}/delete", first.getSeq())
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isForbidden());  // 403
    }

    @Test
    public void testUpdateForm() throws Exception {
        // Given
        board.setWriter(USERNAME);
        final Board saved = boardRepository.save(board);
        // When
        mvc.perform(get("/boards/{seq}/form", saved.getSeq())
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attribute("board", is(saved)));
    }

    @Test
    public void testUpdateForm_OtherUser() throws Exception {
        // Given
        final Board saved = boardRepository.save(board);
        // When
        mvc.perform(get("/boards/{seq}/form", saved.getSeq())
                .with(csrf())
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdate() throws Exception {
        // Given
        board.setWriter(USERNAME);
        final Board saved = boardRepository.save(board);
        final Board willUpdate = random(Board.class);
        willUpdate.setSeq(saved.getSeq());
        willUpdate.setWriter(saved.getWriter());
        willUpdate.setRegDate(null);
        willUpdate.setComments(null);
        MultiValueMap<String, String> params = toMultiValueMap(willUpdate);
        // When
        mvc.perform(post("/boards")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isFound())   // 302
                .andExpect(view().name("redirect:/boards"));
        Board updated = boardRepository.findOne(saved.getSeq());
        assertThat(updated, is(willUpdate));
    }

    @Test
    public void testUpdate_OtherUser() throws Exception {
        // Given
        final Board saved = boardRepository.save(board);
        final Board willUpdate = random(Board.class);
        willUpdate.setSeq(saved.getSeq());
        willUpdate.setWriter(saved.getWriter());
        willUpdate.setRegDate(null);
        willUpdate.setComments(null);
        MultiValueMap<String, String> params = toMultiValueMap(willUpdate);
        // When
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params))
                // Then
                .andExpect(status().isForbidden());   // 302
        Board updated = boardRepository.findOne(saved.getSeq());
        assertThat(updated, is(saved));
    }

    @Test
    @Ignore
    public void testSearch_TitleContaining() throws Exception {
        // Given
        final int count = 10;
        final String searchValue = "searchSEARCH";  // 검색어
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        Board board1 = boards.get(0);   // 제목 검색됨
        board1.setTitle(board1.getTitle() + searchValue + board1.getTitle());
        Iterable<Board> saveds = boardRepository.save(boards);
        // When
        mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML)
                .param("title", searchValue))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attribute("boards", Arrays.asList(board1)))
                .andExpect(model().attribute("search", new BoardSearch(searchValue, null)))
                .andExpect(model().hasNoErrors());
    }

    @Test
    @Ignore
    public void testSearch_TitleIsEmpty() throws Exception {
        // Given
        final int count = 10;
        final String searchValue = "searchSEARCH";  // 검색어
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        Board board1 = boards.get(0);   // 제목 검색됨
        board1.setTitle(board1.getTitle() + searchValue + board1.getTitle());
        Iterable<Board> saveds = boardRepository.save(boards);
        // When
        mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML)
                .param("title", ""))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attribute("boards", Arrays.asList(board1)))
                .andExpect(model().attribute("search", new BoardSearch(searchValue, null)))
                .andExpect(model().hasNoErrors());
    }

    @Test
    @Ignore
    public void testSearch_WriterIs() throws Exception {
        // Given
        final int count = 10;
        final String searchValue = "searchSEARCH";  // 검색어
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        Board board1 = boards.get(0);   // 작성자 검색됨
        board1.setWriter(searchValue);
        Iterable<Board> saveds = boardRepository.save(boards);
        // When
        mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML)
                .param("writer", searchValue))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attribute("boards", Arrays.asList(board1)))
                .andExpect(model().attribute("search", new BoardSearch(null, searchValue)))
                .andExpect(model().hasNoErrors());
    }

//    @Test
//    public void testSearch_TitleContainingAndWriterIs() throws Exception {
//        // Given
//        final int count = 10;
//        final String searchValue = "searchSEARCH";  // 검색어
//        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
//        Board board1 = boards.get(0);   // 제목만 검색됨
//        board1.setTitle(board1.getTitle() + searchValue + board1.getTitle());
//        Board board2 = boards.get(1);   // 작성자만 검색됨
//        board2.setWriter(searchValue);
//        Board board3 = boards.get(2);   // 제목 & 작성자 검색됨
//        board3.setTitle(board3.getTitle() + searchValue + board3.getTitle());
//        board3.setWriter(searchValue);
//        Iterable<Board> saveds = boardRepository.save(boards);
//        // When
//        mvc.perform(get("/boards")
//                .contentType(MediaType.TEXT_HTML)
//                .param("title", searchValue)
//                .param("writer", searchValue))
//                // Then
//                .andExpect(status().isOk())
//                .andExpect(view().name("boards/list"))
//                .andExpect(model().attribute("boards", Arrays.asList(board3)))
//                .andExpect(model().attribute("title", searchValue))
//                .andExpect(model().attribute("writer", searchValue))
//                .andExpect(model().hasNoErrors());
//    }

    @Test
    @Ignore
    public void testSearch_TitleContainingAndWriterIs() throws Exception {
        // Given
        final int count = 10;
        final String searchValue = "searchSEARCH";  // 검색어
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        Board board1 = boards.get(0);   // 제목만 검색됨
        board1.setTitle(board1.getTitle() + searchValue + board1.getTitle());
        Board board2 = boards.get(1);   // 작성자만 검색됨
        board2.setWriter(searchValue);
        Board board3 = boards.get(2);   // 제목 & 작성자 검색됨
        board3.setTitle(board3.getTitle() + searchValue + board3.getTitle());
        board3.setWriter(searchValue);
        Iterable<Board> saveds = boardRepository.save(boards);
        // When
        mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML)
                .param("title", searchValue)
                .param("writer", searchValue))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attribute("page", Arrays.asList(board3)))
                .andExpect(model().attribute("search", new BoardSearch(searchValue, searchValue)))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void testPagingList_Page1() throws Exception {
        // Given
        final int pageSize = 10;
        final int count = 25;
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        List<Board> saveds = boardRepository.save(boards);
        List<Board> pages1 = saveds.stream().limit(pageSize).collect(toList()); // 1페이지 목록
        // When
        mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("search", new BoardSearch(null, null)))
                .andExpect(model().hasNoErrors())
                .andReturn();
    }

    @Test
    public void testSort_Default() throws Exception {
        // Given
        final int count = 25;
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        List<Board> saveds = boardRepository.save(boards);
        Board lastBoard = saveds.get(count - 1);

        // When
        MvcResult mvcResult = mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("search", new BoardSearch(null, null)))
                .andExpect(model().hasNoErrors())
                .andReturn();
        assertSortAndFistElement(mvcResult, "seq", Sort.Direction.DESC, lastBoard);
    }

    private void assertSortAndFistElement(MvcResult mvcResult, String property, Sort.Direction direction,
                                          Board firstElement) {
        @SuppressWarnings("unchecked")
        Page<Board> page = (Page<Board>) mvcResult.getModelAndView().getModelMap().get("page");
        Sort sort = page.getSort();
        assertThat(sort.getOrderFor(property), is(new Sort.Order(direction, property)));
        assertThat(page.getContent().get(0), is(firstElement));
    }

    @Test
    public void testSort_SortWriterAsc() throws Exception {
        // Given
        final int count = 25;
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        List<Board> saveds = boardRepository.save(boards);
        Board firstBoard = saveds.stream()
                .sorted(comparing(Board::getWriter))
                .findFirst().orElse(null);

        // When
        MvcResult mvcResult = mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML)
                .param("sort", "writer"))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("search", new BoardSearch(null, null)))
                .andExpect(model().hasNoErrors())
                .andReturn();
        assertSortAndFistElement(mvcResult, "writer", Sort.Direction.ASC, firstBoard);
    }

    @Test
    public void testSort_SortWriterDesc() throws Exception {
        // Given
        final int count = 25;
        final List<Board> boards = randomListOf(count, Board.class, "seq", "regDate");
        List<Board> saveds = boardRepository.save(boards);
        Board lastBoard = saveds.stream()
                .sorted(comparing(Board::getWriter).reversed())
                .findFirst().orElse(null);

        // When
        MvcResult mvcResult = mvc.perform(get("/boards")
                .contentType(MediaType.TEXT_HTML)
                .param("sort", "writer,desc"))
                // Then
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("search", new BoardSearch(null, null)))
                .andExpect(model().hasNoErrors())
                .andReturn();
        assertSortAndFistElement(mvcResult, "writer", Sort.Direction.DESC, lastBoard);
    }
}
