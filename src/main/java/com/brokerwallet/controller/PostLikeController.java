package com.brokerwallet.controller;

import com.brokerwallet.dto.LikeStatusDTO;
import com.brokerwallet.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
public class PostLikeController {

    @Autowired
    private PostLikeService postLikeService;

    /**
     * 点赞帖子
     */
    @PostMapping
    public ResponseEntity<LikeStatusDTO> likePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {

        LikeStatusDTO response =
                postLikeService.likePost(postId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping
    public ResponseEntity<LikeStatusDTO> unlikePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {

        LikeStatusDTO response =
                postLikeService.unlikePost(postId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 查询点赞状态
     */
    @GetMapping("/status")
    public ResponseEntity<LikeStatusDTO> getLikeStatus(
            @RequestParam Long postId,
            @RequestParam Long userId) {

        LikeStatusDTO response =
                postLikeService.getLikeStatus(postId, userId);

        return ResponseEntity.ok(response);
    }

}