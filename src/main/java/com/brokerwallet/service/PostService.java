package com.brokerwallet.service;

import com.brokerwallet.dto.PostDTO;
import com.brokerwallet.entity.Post;
import com.brokerwallet.entity.UserAccount;
import com.brokerwallet.repository.PostRepository;
import com.brokerwallet.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        PostDTO dto = buildBaseDTO(post);
        userAccountRepository.findById(post.getUserId())
                .ifPresent(user -> fillUserInfo(dto, user));

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
        post.setRewardAmount(BigDecimal.ZERO);

        Post saved = postRepository.save(post);

        PostDTO dto = buildBaseDTO(saved);

        userAccountRepository.findById(saved.getUserId())
                .ifPresent(user -> dto.setUserName(user.getUsername()));

        return dto;
    }

    /**
     * 分页获取全部帖子
     */
    public Page<PostDTO> getAllPosts(Pageable pageable) {

        Page<Post> postPage = postRepository.findAllByOrderByCreateTimeDesc(pageable);

        return postPage.map(post -> {
            PostDTO dto = buildBaseDTO(post);
            userAccountRepository.findById(post.getUserId())
                    .ifPresent(user -> fillUserInfo(dto, user));
            return dto;
        });
    }

    /**
     * 分页获取用户帖子（
     */
    public Page<PostDTO> getUserPosts(Long userId, Pageable pageable) {

        Page<Post> page = postRepository
                .findByUserIdOrderByCreateTimeDesc(userId, pageable);
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return page.map(post -> convertToDTO(post, user));
    }

    /**
     * 删除帖子
     */
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }


    // ================== 通用方法 ==================
    /**
     * 构建基础 DTO
     */
    private PostDTO buildBaseDTO(Post post) {

        PostDTO dto = new PostDTO();

        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());

        dto.setLikeCount(post.getLikeCount() != null ? post.getLikeCount() : 0);
        dto.setCommentCount(post.getCommentCount());
        dto.setRewardAmount(
                post.getRewardAmount() == null
                        ? BigDecimal.ZERO
                        : post.getRewardAmount().stripTrailingZeros()
        );

        dto.setCreateTime(post.getCreateTime());

        //图片统一处理
        List<String> images = parseImages(post.getImages());
        dto.setImages(images);
        //dto.setFirstImageUrl(images.isEmpty() ? null : images.get(0));

        return dto;
    }

    /**
     * 填充用户信息
     */
    private void fillUserInfo(PostDTO dto, UserAccount user) {
        dto.setUserName(user.getUsername());
        dto.setAvatarUrl(user.getAvatar());
        dto.setAddress(user.getWalletAddress());
    }

    /**
     * 用户分页专用 DTO 构建（带 user）
     */
    private PostDTO convertToDTO(Post post, UserAccount user) {
        PostDTO dto = buildBaseDTO(post);
        fillUserInfo(dto, user);
        return dto;
    }

    /**
     * 图片解析
     */
    private List<String> parseImages(String images) {
        if (images == null || images.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(images.split(","));
    }
}