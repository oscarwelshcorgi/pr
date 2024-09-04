package com.pr.board.service;

import com.pr.board.domain.Article;
import com.pr.board.domain.BoardRepositoryCustom;
import com.pr.board.dto.BoardDto;
import com.pr.board.model.Header;
import com.pr.board.model.Pagination;
import com.pr.board.repository.BoardRepository;
import com.pr.config.SearchCondition;
import com.pr.member.domain.MemberInfo;
import com.pr.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final MemberRepository memberRepository;

    // 게시글 리스트 조회
    public Header<List<BoardDto>> getBoardList(Pageable pageable, SearchCondition searchCondition) {
        Page<Article> boardPage = boardRepositoryCustom.findAllBySearchCondition(pageable, searchCondition);
        // board_code가 "humor"인 게시글만 조회
        List<Article> articles = boardRepository.findByBoardCodeAndDeleteYn("humor", "n");
        // 필터링된 결과를 페이지 처리합니다.
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), articles.size());
        List<Article> pagedArticles = articles.subList(start, end);

        // Board 엔티티 리스트를 BoardDto 리스트로 변환하여 반환
        List<BoardDto> dtos = boardPage.stream()
                .map(board -> convertToDto(board, null, null))
                .collect(Collectors.toList());

        // 페이징 정보 설정
        Pagination pagination = new Pagination(
                (int) boardPage.getTotalElements(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                5
        );
        return Header.OK(dtos, pagination);
    }

    // 게시글 상세 조회
    public BoardDto getBoardDetail(Long id) {
        // ID로 게시글을 조회하여 BoardDto로 변환하여 반환
        Article article = findBoardById(id);
        Long nextBoardId = boardRepository.findNextBoardId(article.getId()).orElse(null);
        Long previousBoardId = boardRepository.findPreviousBoardId(article.getId()).orElse(null);
        return convertToDto(article, nextBoardId, previousBoardId);
    }

    // 게시글 조회수 증가
    public BoardDto getIncreaseViewCount(Long id) {
        // ID로 게시글을 조회하여 BoardDto로 변환하여 반환
        Article article = findBoardById(id);
        article.setViewCount(article.getViewCount() + 1); // 조회수 증가
        System.out.println("ViewCount 조회수 증가!!!!: " + article.getViewCount());
        return convertToDto(article, null, null);
    }

    // 게시글 등록
    public BoardDto createBoard(BoardDto boardDto) {
        //boardCode를 humor로 insert
        boardDto.setBoardCode("humor");
        // BoardDto를 Board 엔티티로 변환하여 저장
        Article article = convertToEntity(boardDto);
        Article savedArticle = boardRepository.save(article);

        // 저장된 게시글의 ID로 상세 정보 조회하여 반환
        return getBoardDetail(savedArticle.getId());
    }

    // 게시글 수정
    public BoardDto updateBoard(BoardDto boardDto) {
        // ID로 게시글을 조회하여 수정하고 저장 후 BoardDto로 변환하여 반환
        Article article = findBoardById(boardDto.getId());
        article.setTitle(boardDto.getTitle());
        article.setContent(boardDto.getContent());
        boardRepository.save(article);
        return convertToDto(article, null, null);
    }

    // 게시글 삭제

    public void deleteBoard(Long id) {
        // Find the board by ID
        Optional<Article> boardOptional = boardRepository.findById(id);

        if (boardOptional.isPresent()) {
            Article article = boardOptional.get();
            // Update deleteYn to "y"
            article.setDeleteYn("y");
            // Save the updated board
            boardRepository.save(article);
        } else {
            throw new RuntimeException("Board not found with id: " + id);
        }
    }

    // 게시글 유무 확인
    private Article findBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    // Entity -> DTO 변환 메서드
    private BoardDto convertToDto(Article article, Long nextBoardId, Long previousBoardId) {
        return BoardDto.builder()
                .id(article.getId())
                .nickName(article.getMemberInfo().getNickName()) // member의 nickName 가져오기
                .email(article.getEmail())
                .title(article.getTitle())
                .content(article.getContent())
                .createDate(article.getCreateDate())
                .viewCount(article.getViewCount())
                .nextBoardId(nextBoardId)
                .previousBoardId(previousBoardId)
                .boardCode(article.getBoardCode())
                .build();
    }

    // DTO -> Entity 변환 메서드
    private Article convertToEntity(BoardDto boardDto) {
        MemberInfo memberInfo = memberRepository.findByEmail(boardDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + boardDto.getEmail()));

        return Article.builder()
                .memberInfo(memberInfo)
                .email(boardDto.getEmail())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .createDate(boardDto.getCreateDate()) // 생성일은 클라이언트에서 전달된 값 사용
                .viewCount(boardDto.getViewCount())
                .boardCode(boardDto.getBoardCode())
                .build();
    }

    public Header<List<BoardDto>> getBoardList(Pageable pageable) {
        List<BoardDto> dtos = new ArrayList<>();

        Page<Article> boardEntities = boardRepository.findAllByOrderByIdDesc(pageable);
        for (Article entity : boardEntities) {
            BoardDto dto = BoardDto.builder()
                    .id(entity.getId())
                    .nickName(entity.getMemberInfo().getNickName()) // Member 엔티티를 통해 nickName 접근
                    .title(entity.getTitle())
                    .content(entity.getContent())
                    .createDate(entity.getCreateDate())
                    .viewCount(entity.getViewCount())
                    .boardCode(entity.getBoardCode())
                    .build();

            dtos.add(dto);
        }

        Pagination pagination = new Pagination(
                (int) boardEntities.getTotalElements()
                , pageable.getPageNumber() + 1
                , pageable.getPageSize()
                , 10
        );

        return Header.OK(dtos, pagination);
    }



}