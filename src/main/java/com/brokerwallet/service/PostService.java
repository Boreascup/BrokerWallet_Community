package com.brokerwallet.service;

import com.brokerwallet.repository.PostRepository;
import com.brokerwallet.dto.PostDTO;
import com.brokerwallet.entity.Post;
import com.brokerwallet.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    /**
     * 根据ID获取帖子
     */
    public PostDTO getPost(Long id) {

        Post post = postRepository.findById(id).orElseThrow();

        PostDTO dto = new PostDTO();

        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setLikeCount(post.getLikeCount());
        dto.setCreateTime(post.getCreateTime());
        userAccountRepository.findById(post.getUserId())
                .ifPresent(user -> dto.setUserName(user.getUsername()));

        return dto;
    }

    /**
     * 创建帖子
     */
    public PostDTO createPost(PostDTO postDTO) {

        Post post = new Post();

        post.setUserId(postDTO.getUserId());
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCreateTime(LocalDateTime.now());
        post.setLikeCount(0);
        post.setRewardAmount(BigDecimal.valueOf(0));

        Post savedPost = postRepository.save(post);

        PostDTO response = new PostDTO();
        response.setId(savedPost.getId());
        response.setUserId(savedPost.getUserId());
        response.setTitle(savedPost.getTitle());
        response.setContent(savedPost.getContent());
        response.setLikeCount(savedPost.getLikeCount());
        response.setCreateTime(savedPost.getCreateTime());
        response.setRewardAmount(savedPost.getRewardAmount());
        userAccountRepository.findById(post.getUserId())
                .ifPresent(user -> response.setUserName(user.getUsername()));

        return response;
    }

    /**
     * 分页获取全部帖子
     */
    public Page<PostDTO> getAllPosts(Pageable pageable) {

        // 1. 分页查询帖子数据
        Page<Post> postPage = postRepository.findAllByOrderByCreateTimeDesc(pageable);

        // 2. 转换为 DTO，填充所有字段
        return postPage.map(post -> {
            PostDTO dto = new PostDTO();

            // 基础帖子信息
            dto.setId(post.getId());
            dto.setUserId(post.getUserId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setLikeCount(post.getLikeCount() != null ? post.getLikeCount() : 0);
            dto.setCommentCount(post.getCommentCount());
            dto.setRewardAmount(post.getRewardAmount());
            dto.setCreateTime(post.getCreateTime());

            // 3. 处理用户信息：用户名 + 头像
            userAccountRepository.findById(post.getUserId()).ifPresent(user -> {
                dto.setUserName(user.getUsername());
                dto.setAvatarUrl(user.getAvatar());
            });

            // 4. 处理图片：数据库 String 转 List<String>
            String imagesStr = post.getImages();
            if (imagesStr != null && !imagesStr.trim().isEmpty()) {
                // 按逗号分割图片路径，支持多图
                List<String> imageList = Arrays.asList(imagesStr.split(","));
                dto.setImages(imageList);
            } else {
                // 空值处理，避免 null 异常
                dto.setImages(Collections.emptyList());
            }

            return dto;
        });
    }

    /**
     * 获取用户的帖子
     */
    public List<Post> findByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    /**
     * 删除帖子
     */
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
