package com.brokerwallet.service;

import com.brokerwallet.repository.PostRepository;
import com.brokerwallet.dto.PostDTO;
import com.brokerwallet.entity.Post;
import com.brokerwallet.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

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
        post.setRewardSum(BigDecimal.valueOf(0));

        Post savedPost = postRepository.save(post);

        PostDTO response = new PostDTO();
        response.setId(savedPost.getId());
        response.setUserId(savedPost.getUserId());
        response.setTitle(savedPost.getTitle());
        response.setContent(savedPost.getContent());
        response.setLikeCount(savedPost.getLikeCount());
        response.setCreateTime(savedPost.getCreateTime());
        response.setRewardSum(savedPost.getRewardSum());
        userAccountRepository.findById(post.getUserId())
                .ifPresent(user -> response.setUserName(user.getUsername()));

        return response;
    }

    /**
     * 分页获取全部帖子
     */
    public Page<PostDTO> getAllPosts(Pageable pageable) {

        Page<Post> postPage = postRepository
                .findAllByOrderByCreateTimeDesc(pageable);

        return postPage.map(post -> {

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
