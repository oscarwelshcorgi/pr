package com.pr.humor.board.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagination {
    private int pageSize;         // 페이지당 보여지는 게시글 최대 개수
    private int page;             // 현재 페이지
    private int block;            // 현재 블럭
    private int totalListCnt;     // 총 게시글 수
    private int totalPageCnt;     // 총 페이지 수
    private int totalBlockCnt;    // 총 블럭 수
    private int startPage;        // 시작 페이지
    private int endPage;          // 마지막 페이지
    private int prevBlock;        // 이전 구간 마지막 페이지
    private int nextBlock;        // 다음 구간 시작 페이지
    private int startIndex;       // 시작 인덱스

    public Pagination(Integer totalListCnt, Integer page, Integer pageSize, Integer blockSize) {
        // 페이지당 보여지는 게시글 최대 개수
        this.pageSize = pageSize;

        //현재 페이지
        this.page = page;

        //총 게시글 수
        this.totalListCnt = totalListCnt;

        calculatePagination(blockSize);
    }

    private void calculatePagination(int blockSize){
        // 총 페이지 수 계산
        totalPageCnt = (int) Math.ceil((double) totalListCnt / pageSize);

        // 총 블럭 수 계산
        totalBlockCnt = (int) Math.ceil((double) totalPageCnt / blockSize);

        // 현재 블럭 계산
        block = (int) Math.ceil((double) page / blockSize);

        // 블럭 시작 페이지 계산
        startPage = (block - 1) * blockSize + 1;

        // 블럭 마지막 페이지 계산
        endPage = Math.min(startPage + blockSize - 1, totalPageCnt);

        // 이전 블럭 계산
        prevBlock = Math.max((block - 1) * blockSize, 1);

        // 다음 블럭 계산
        nextBlock = Math.min(block * blockSize + 1, totalPageCnt);

        // 시작 인덱스 계산
        startIndex = (page - 1) * pageSize;
    }
}