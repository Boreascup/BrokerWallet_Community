package com.brokerwallet.repository;

import com.brokerwallet.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 判断用户是否已经点赞
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 删除点赞（取消点赞）
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);

    /**
     * 统计某个帖子的点赞数
     */
    long countByPostId(Long postId);

}
