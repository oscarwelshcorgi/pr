package com.pr.humor.board.controller;

import com.pr.config.SearchCondition;
import com.pr.humor.board.dto.HumorBoardDto;
import com.pr.humor.board.model.Header;
import com.pr.humor.board.service.HumorBoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class HumorBoardController {

    private final HumorBoardService humorBoardService;
    private final HttpSession httpSession;

    // 공통된 리스트 조회 로직 (유머, 썰톡)
    private Header<List<HumorBoardDto>> getBoardList(@PageableDefault(sort = {"id"}) Pageable pageable, SearchCondition searchCondition, String boardCode) {
        if ("humor".equals(boardCode)) {
            return humorBoardService.getHumorBoardList(pageable, searchCondition);
        } else if ("ssul".equals(boardCode)) {
            return humorBoardService.getSsulBoardList(pageable, searchCondition);
        }
        throw new IllegalArgumentException("Invalid board code: " + boardCode);
    }

    // 유머 게시판 리스트 조회
    @GetMapping("/humorBoard/list")
    public Header<List<HumorBoardDto>> humorBoardList(@PageableDefault(sort = {"id"}) Pageable pageable, SearchCondition searchCondition) {
        return getBoardList(pageable, searchCondition, "humor");
    }

    // 썰톡 게시판 리스트 조회
    @GetMapping("/ssulBoard/list")
    public Header<List<HumorBoardDto>> ssulBoardList(@PageableDefault(sort = {"id"}) Pageable pageable, SearchCondition searchCondition) {
        return getBoardList(pageable, searchCondition, "ssul");
    }

    // 공통된 상세 조회 로직 (유머, 썰톡)
    private HumorBoardDto getBoardDetail(Long id, String boardCode) {
        if ("humor".equals(boardCode)) {
            return humorBoardService.getHumorBoardDetail(id);
        } else if ("ssul".equals(boardCode)) {
            return humorBoardService.getSsulBoardDetail(id);
        }
        throw new IllegalArgumentException("Invalid board code: " + boardCode);
    }

    // 유머 게시글 조회
    @GetMapping("/humorBoard/detail/{id}")
    public HumorBoardDto humorBoardDetail(@PathVariable("id") Long id) {
        return getBoardDetail(id, "humor");
    }

    // 썰톡 게시글 조회
    @GetMapping("/ssulBoard/detail/{id}")
    public HumorBoardDto ssulBoardDetail(@PathVariable("id") Long id) {
        return getBoardDetail(id, "ssul");
    }

    // 공통된 게시글 작성 로직 (유머, 썰톡)
    private HumorBoardDto createBoard(HumorBoardDto humorBoardDto, String boardCode) {
        if ("humor".equals(boardCode)) {
            return humorBoardService.createHumorBoard(humorBoardDto);
        } else if ("ssul".equals(boardCode)) {
            return humorBoardService.createSsulBoard(humorBoardDto);
        }
        throw new IllegalArgumentException("Invalid board code: " + boardCode);
    }

    // 유머 게시글 작성
    @PostMapping("/humorBoard/create")
    public HumorBoardDto createHumorBoard(@RequestBody HumorBoardDto humorBoardDto) {
        return createBoard(humorBoardDto, "humor");
    }

    // 썰톡 게시글 작성
    @PostMapping("/ssulBoard/create")
    public HumorBoardDto createSsulBoard(@RequestBody HumorBoardDto humorBoardDto) {
        return createBoard(humorBoardDto, "ssul");
    }

    // 공통된 게시글 수정 로직 (유머, 썰톡)
    @PostMapping("/humorBoard/update")
    public HumorBoardDto updateHumorBoard(@RequestBody HumorBoardDto humorBoardDto) {
        return humorBoardService.updateBoardDetails(humorBoardDto);
    }

    @PutMapping("/ssulBoard/update")
    public HumorBoardDto updateSsulBoard(@RequestBody HumorBoardDto humorBoardDto) {
        return humorBoardService.updateBoardDetails(humorBoardDto);
    }

    // 게시글 삭제 (유머, 썰톡)
    @PostMapping("/humorBoard/delete/{id}")
    public void deleteHumorBoard(@PathVariable("id") Long id) {
        humorBoardService.deleteBoardById(id);
    }

    @PostMapping("/ssulBoard/delete/{id}")
    public void deleteSsulBoard(@PathVariable("id") Long id) {
        humorBoardService.deleteBoardById(id);
    }

    // 공통된 조회수 증가 로직 (유머, 썰톡)
    @PutMapping("/increaseViewCount")
    private HumorBoardDto increaseViewCount(Long id) {
        return humorBoardService.increaseViewCount(id);
    }

    //예전 게시판 백업 1
    @GetMapping(value="/usr/article/m.detail/{id}")
    public HumorBoardDto boardDetailBackup(@PathVariable("id") Long id) {
        return getBoardDetail(id, "humor");
    }

    //예전 게시판 백업 2
    @GetMapping(value="/board/boardDetail/{id}")
    public HumorBoardDto boardDetailBackup2(@PathVariable("id") Long id) {
        return getBoardDetail(id, "humor");
    }

}
