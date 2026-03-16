package com.brokerwallet.controller;

import com.brokerwallet.dto.PostDTO;
import com.brokerwallet.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 创建帖子
     */
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {

        PostDTO response = postService.createPost(postDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取单个帖子
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {

        PostDTO response = postService.getPost(id);

        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取所有帖子
     */
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostDTO> response = postService.getAllPosts(pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {

        postService.deletePost(id);

        return ResponseEntity.ok().build();
    }

}
