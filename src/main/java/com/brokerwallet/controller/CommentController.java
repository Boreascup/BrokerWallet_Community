package com.brokerwallet.controller;

import com.brokerwallet.common.result.Result;
import com.brokerwallet.dto.CommentDTO;
import com.brokerwallet.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping
    public Result<CommentDTO> createComment(
            @RequestBody CommentDTO commentDTO) {

        CommentDTO response = commentService.createComment(commentDTO);

        return Result.ok(response);
    }

    /**
     * 分页获取某个帖子的评论
     */
    @GetMapping("/post/{postId}")
    public Result<Page<CommentDTO>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<CommentDTO> response =
                commentService.getCommentsByPostId(postId, pageable);

        return Result.ok(response);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(
            @PathVariable Long id) {

        commentService.deleteComment(id);

        return Result.ok(null, "删除评论成功");
    }

}
