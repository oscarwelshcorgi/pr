package com.pr.humor.board.repository;


import com.pr.humor.board.domain.HumorComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HumorCommentRepository extends JpaRepository<HumorComment, Long> {
    List<HumorComment> findByHumorArticle_IdAndDeleteYn(Long articleId, String deleteYn);
}