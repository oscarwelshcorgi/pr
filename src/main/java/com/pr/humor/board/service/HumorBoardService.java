package com.pr.humor.board.service;

import com.pr.config.SearchCondition;
import com.pr.humor.board.domain.HumorArticle;
import com.pr.humor.board.domain.HumorBoardRepositoryCustom;
import com.pr.humor.board.dto.HumorBoardDto;
import com.pr.humor.board.model.Header;
import com.pr.humor.board.model.Pagination;
import com.pr.humor.board.repository.HumorArticleRepository;
import com.pr.member.domain.MemberInfo;
import com.pr.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HumorBoardService {

    private final HumorArticleRepository humorArticleRepository;
    private final HumorBoardRepositoryCustom humorBoardRepositoryCustom;
    private final MemberRepository memberRepository;

    // 공통 게시글 리스트 조회 (boardCode만 다름)
    private Header<List<HumorBoardDto>> getBoardListByBoardCode(Pageable pageable, String boardCode) {
        // boardCode와 deleteYn 조건에 맞는 게시글 조회 및 페이징 처리
        Page<HumorArticle> boardPage = humorArticleRepository.findByBoardCodeAndDeleteYnOrderByIdDesc(boardCode, "n", pageable);

        // Board 엔티티 리스트를 BoardDto 리스트로 변환하여 반환
        List<HumorBoardDto> dtos = boardPage.stream()
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

    // 유머 게시글 리스트 조회
    public Header<List<HumorBoardDto>> getHumorBoardList(Pageable pageable, SearchCondition searchCondition) {
        return getBoardListByBoardCode(pageable, "humor");
    }

    // 썰톡 게시글 리스트 조회
    public Header<List<HumorBoardDto>> getSsulBoardList(Pageable pageable, SearchCondition searchCondition) {
        return getBoardListByBoardCode(pageable, "ssul");
    }

    // 공통 게시글 상세 조회
    private HumorBoardDto getBoardDetailByBoardCode(Long id, String boardCode) {
        HumorArticle humorArticle = findBoardById(id);

        // 이전, 다음 게시글 ID를 서비스 내에서 처리
        Long nextBoardId = humorArticleRepository.findNextHumorId(humorArticle.getId(), humorArticle.getBoardCode()).orElse(null);
        Long previousBoardId = humorArticleRepository.findPreviousHumorId(humorArticle.getId(), humorArticle.getBoardCode()).orElse(null);

        return convertToDto(humorArticle, nextBoardId, previousBoardId);
    }

    // 유머 게시글 상세 조회
    public HumorBoardDto getHumorBoardDetail(Long id) {
        return getBoardDetailByBoardCode(id, "humor");
    }

    // 썰톡 게시글 상세 조회
    public HumorBoardDto getSsulBoardDetail(Long id) {
        return getBoardDetailByBoardCode(id, "ssul");
    }

    // 게시글 조회수 증가
    public HumorBoardDto increaseViewCount(Long id) {
        HumorArticle humorArticle = findBoardById(id);
        humorArticle.setViewCount(humorArticle.getViewCount() + 1); // 조회수 증가
        return convertToDto(humorArticle, null, null);
    }

    // 유머 게시글 등록
    public HumorBoardDto createHumorBoard(HumorBoardDto humorBoardDto) {
        return createBoard(humorBoardDto, "humor");
    }

    // 썰톡 게시글 등록
    public HumorBoardDto createSsulBoard(HumorBoardDto humorBoardDto) {
        return createBoard(humorBoardDto, "ssul");
    }

    // 게시글 등록 공통 로직
    private HumorBoardDto createBoard(HumorBoardDto humorBoardDto, String boardCode) {
        humorBoardDto.setBoardCode(boardCode);
        HumorArticle humorArticle = convertToEntity(humorBoardDto);
        HumorArticle savedHumorArticle = humorArticleRepository.save(humorArticle);
        return getBoardDetailByBoardCode(savedHumorArticle.getId(), boardCode);
    }

    // 게시글 수정
    public HumorBoardDto updateBoardDetails(HumorBoardDto humorBoardDto) {
        HumorArticle humorArticle = findBoardById(humorBoardDto.getId());
        humorArticle.setTitle(humorBoardDto.getTitle());
        humorArticle.setContent(humorBoardDto.getContent());
        humorArticleRepository.save(humorArticle);
        return convertToDto(humorArticle, null, null);
    }

    // 게시글 삭제
    public void deleteBoardById(Long id) {
        HumorArticle humorArticle = findBoardById(id);
        humorArticle.setDeleteYn("y");
        humorArticleRepository.save(humorArticle);
    }

    // 게시글 유무 확인
    private HumorArticle findBoardById(Long id) {
        return humorArticleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    // Entity -> DTO 변환 메서드
    private HumorBoardDto convertToDto(HumorArticle humorArticle, Long nextBoardId, Long previousBoardId) {
        return HumorBoardDto.builder()
                .id(humorArticle.getId())
                .nickName(humorArticle.getMemberInfo().getNickName()) // member의 nickName 가져오기
                .email(humorArticle.getEmail())
                .title(humorArticle.getTitle())
                .content(humorArticle.getContent())
                .createDate(humorArticle.getCreateDate())
                .viewCount(humorArticle.getViewCount())
                .nextBoardId(nextBoardId)
                .previousBoardId(previousBoardId)
                .boardCode(humorArticle.getBoardCode())
                .build();
    }

    // DTO -> Entity 변환 메서드
    private HumorArticle convertToEntity(HumorBoardDto humorBoardDto) {
        MemberInfo memberInfo = memberRepository.findByEmail(humorBoardDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + humorBoardDto.getEmail()));

        return HumorArticle.builder()
                .memberInfo(memberInfo)
                .email(humorBoardDto.getEmail())
                .title(humorBoardDto.getTitle())
                .content(humorBoardDto.getContent())
                .createDate(humorBoardDto.getCreateDate()) // 생성일은 클라이언트에서 전달된 값 사용
                .viewCount(humorBoardDto.getViewCount())
                .boardCode(humorBoardDto.getBoardCode())
                .build();
    }
}
