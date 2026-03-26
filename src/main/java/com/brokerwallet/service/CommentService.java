package com.brokerwallet.service;

import com.brokerwallet.dto.PostDTO;
import com.brokerwallet.repository.CommentRepository;
import com.brokerwallet.dto.CommentDTO;
import com.brokerwallet.entity.Comment;
import com.brokerwallet.repository.PostRepository;
import com.brokerwallet.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository commentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PostRepository postRepository;

    /**
     * 创建评论
     */
    @Transactional
    public CommentDTO createComment(CommentDTO commentDTO) {

        Comment comment = new Comment();
        comment.setPostId(commentDTO.getPostId());
        comment.setUserId(commentDTO.getUserId());
        comment.setContent(commentDTO.getContent());
        comment.setCreateTime(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        // 增加帖子的评论数
        int updated = postRepository.incrementCommentCount(saved.getPostId());
        if (updated == 0) {
            throw new RuntimeException("帖子不存在，评论数更新失败");
        }

        CommentDTO response = new CommentDTO();
        response.setId(saved.getId());
        response.setPostId(saved.getPostId());
        response.setContent(saved.getContent());
        response.setUserId(saved.getUserId());
        response.setCreateTime(saved.getCreateTime());

        userAccountRepository.findById(saved.getUserId())
                .ifPresent(user -> response.setUserName(user.getUsername()));

        return response;
    }

    /**
     * 分页获取评论（升序
     */
    public Page<CommentDTO> getCommentsByPostId(Long postId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByPostIdOrderByCreateTimeAsc(postId, pageable);

        return commentPage.map(comment -> {

            CommentDTO dto = new CommentDTO();

            dto.setId(comment.getId());
            dto.setPostId(comment.getPostId());
            dto.setUserId(comment.getUserId());
            dto.setContent(comment.getContent());
            userAccountRepository.findById(comment.getUserId())
                    .ifPresent(user -> dto.setUserName(user.getUsername()));
            dto.setCreateTime(comment.getCreateTime());

            return dto;
        });
    }

    /**
     * 删除评论
     */
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

}
