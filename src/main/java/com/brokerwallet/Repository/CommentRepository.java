package com.brokerwallet.Repository;

import com.brokerwallet.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据帖子ID查询评论
     */
    List<Comment> findByPostId(Long postId);

    /**
     * 升序分页查询某个帖子的评论
     */
    Page<Comment> findByPostIdOrderByCreateTimeAsc(Long postId, Pageable pageable);

    /**
     * 删除某个帖子的所有评论
     */
    void deleteByPostId(Long postId);

}