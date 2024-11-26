package com.pr.humor.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumorBoardDto {
    private Long id;
    private String title;
    private String content;
    private String email;
    private String nickName; // member 테이블의 nickName 가져옴
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yy.MM.dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String deleteYn;
    private int viewCount;
    private Long previousBoardId;
    private Long nextBoardId;
    private String boardCode;
}