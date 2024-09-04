package com.pr.board.repository;

import com.pr.board.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Article, Long> {

    Optional<Article> findById(Long id);

    //List<Board> findAll();
    Page<Article> findAllByOrderByIdDesc(Pageable pageable);

    // board_code, delete_yn이 특정 값인 게시글 조회
    List<Article> findByBoardCodeAndDeleteYn(String boardCode, String deleteYn);

    @Query("SELECT MAX(b.id) FROM Board b WHERE b.id < ?1")
    Optional<Long> findPreviousBoardId(Long currentId); // 이전 게시글(예] 현재 id=10 일 때, 11로 이동)
    @Query("SELECT MIN(b.id) FROM Board b WHERE b.id > ?1")
    Optional<Long> findNextBoardId(Long currentId); // 다음 게시글(예] 현재 id=10 일 때, 9로 이동)
}
