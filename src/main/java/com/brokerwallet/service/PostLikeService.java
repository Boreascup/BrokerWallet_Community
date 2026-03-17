package com.brokerwallet.service;

import com.brokerwallet.repository.PostLikeRepository;
import com.brokerwallet.repository.PostRepository;
import com.brokerwallet.dto.LikeStatusDTO;
import com.brokerwallet.entity.Post;
import com.brokerwallet.entity.PostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    /**
     * 点赞
     */
    public LikeStatusDTO likePost(Long postId, Long userId) {

        boolean liked = postLikeRepository
                .existsByPostIdAndUserId(postId, userId);

        if (!liked) {

            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);

            postLikeRepository.save(like);

            // 原子更新点赞数
            postRepository.incrementLikeCount(postId);

            liked = true;
        }

        int likeCount = postRepository
                .findById(postId)
                .map(Post::getLikeCount)
                .orElse(0);

        return new LikeStatusDTO(
                postId,
                userId,
                liked,
                likeCount
        );
    }

    /**
     * 取消点赞
     */
    public LikeStatusDTO unlikePost(Long postId, Long userId) {

        boolean liked = postLikeRepository
                .existsByPostIdAndUserId(postId, userId);

        if (liked) {

            postLikeRepository.deleteByPostIdAndUserId(postId, userId);

            postRepository.decrementLikeCount(postId);

            liked = false;
        }

        int likeCount = postRepository
                .findById(postId)
                .map(Post::getLikeCount)
                .orElse(0);

        return new LikeStatusDTO(
                postId,
                userId,
                liked,
                likeCount
        );
    }

    /**
     * 查询点赞状态
     */
    public LikeStatusDTO getLikeStatus(Long postId, Long userId) {

        boolean liked = postLikeRepository
                .existsByPostIdAndUserId(postId, userId);

        int likeCount = postRepository
                .findById(postId)
                .map(Post::getLikeCount)
                .orElse(0);

        return new LikeStatusDTO(
                postId,
                userId,
                liked,
                likeCount
        );
    }

}