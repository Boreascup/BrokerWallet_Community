package com.brokerwallet.Repository;

import com.brokerwallet.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * 根据用户ID查询帖子
     */
    List<Post> findByUserId(Long userId);

    /**
     * 按创建时间倒序查询帖子
     */
    List<Post> findAllByOrderByCreateTimeDesc();

    /**
     * 分页查询帖子
     */
    Page<Post> findAllByOrderByCreateTimeDesc(Pageable pageable);


    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId AND p.likeCount > 0")
    void decrementLikeCount(@Param("postId") Long postId);
}
