package com.pr.board.domain;

import com.pr.member.domain.MemberInfo;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "article")
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
    private Long id;
    @Column(name ="title", nullable = false)
    private String title;
    @Column(name ="content", nullable = false)
    private String content;
    @Column(name ="email", nullable = false)
    private String email;
    @Column(name ="createDate", nullable = false)
    private LocalDateTime createDate;
    @Column(name ="updateDate")
    private LocalDateTime updateDate;
    @Column(name ="deleteYn", nullable = false)
    private String deleteYn;
    @Column(name = "viewCount", nullable = true)
    private int viewCount;
    @Column(name = "boardCode", nullable = false)
    private String boardCode;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private MemberInfo memberInfo;

    @Builder
    public Article(Long id, String title, String content, String email, LocalDateTime createDate, LocalDateTime updateDate, String deleteYn, int viewCount, String boardCode, MemberInfo memberInfo) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.email = email;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.deleteYn = deleteYn;
        this.viewCount = viewCount;
        this.boardCode = boardCode;
        this.memberInfo = memberInfo;
    }

    @PrePersist
    public void prePersist() {
        this.createDate = LocalDateTime.now(); // 엔티티가 저장되기 전에 작성 날짜 설정
        this.deleteYn = "n";
    }
}