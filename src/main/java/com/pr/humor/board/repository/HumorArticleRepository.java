package com.pr.humor.board.repository;

import com.pr.humor.board.domain.HumorArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HumorArticleRepository extends JpaRepository<HumorArticle, Long> {

    Optional<HumorArticle> findById(Long id);

    //List<Board> findAll();
    Page<HumorArticle> findAllByOrderByIdDesc(Pageable pageable);

    // board_code, delete_yn이 특정 값인 게시글 조회
    Page<HumorArticle> findByBoardCodeAndDeleteYnOrderByIdDesc(String boardCode, String deleteYn, Pageable pageable);

    // 유머 이전, 다음 게시글 조회
    @Query("SELECT MAX(h.id) FROM HumorArticle h WHERE h.id < :id AND h.boardCode = :boardCode AND h.deleteYn = 'n'")
    Optional<Long> findPreviousHumorId(@Param("id") Long id, @Param("boardCode") String boardCode);
    @Query("SELECT MIN(h.id) FROM HumorArticle h WHERE h.id > :id AND h.boardCode = :boardCode AND h.deleteYn = 'n'")
    Optional<Long> findNextHumorId(@Param("id") Long id, @Param("boardCode") String boardCode); // 다음 게시글(예] 현재 id=10 일 때, 9로 이동)

}
