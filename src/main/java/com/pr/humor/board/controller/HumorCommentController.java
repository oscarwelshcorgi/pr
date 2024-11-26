package com.pr.humor.board.controller;

import com.pr.humor.board.domain.HumorComment;
import com.pr.humor.board.service.HumorCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/humorComments")
public class HumorCommentController {

    @Autowired
    private HumorCommentService humorCommentService;

    @RequestMapping(value="/article/{articleId}", method= RequestMethod.GET)
    public List<HumorComment> getCommentsByArticleId(@PathVariable Long articleId) {
        return humorCommentService.getCommentsByArticleId(articleId);
    }

    @PostMapping
    public HumorComment addComment(@RequestBody HumorComment humorComment) {
        return humorCommentService.addComment(humorComment);
    }
}