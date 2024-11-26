package com.pr.humor.board.service;

import com.pr.humor.board.domain.HumorArticle;
import com.pr.humor.board.domain.HumorComment;
import com.pr.humor.board.repository.HumorArticleRepository;
import com.pr.humor.board.repository.HumorCommentRepository;
import com.pr.member.domain.MemberInfo;
import com.pr.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HumorCommentService {

    @Autowired
    private HumorCommentRepository humorCommentRepository;

    @Autowired
    private HumorArticleRepository humorArticleRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<HumorComment> getCommentsByArticleId(Long articleId) {
        List<HumorComment> humorComments = humorCommentRepository.findByHumorArticle_IdAndDeleteYn(articleId, "N");
        for (HumorComment humorComment : humorComments) {
            MemberInfo memberInfo = memberRepository.findByEmail(humorComment.getEmail())
                    .orElse(null);
            if (memberInfo != null) {
                humorComment.setNickName(memberInfo.getNickName());
            }
        }
        return humorComments;
    }

    public HumorComment addComment(HumorComment humorComment) {
        HumorArticle humorArticle = humorArticleRepository.findById(humorComment.getHumorArticle().getId())
                .orElseThrow(() -> new NoSuchElementException("Article with ID " + humorComment.getHumorArticle().getId() + " not found"));
        humorComment.setHumorArticle(humorArticle);
        return humorCommentRepository.save(humorComment);
    }
}