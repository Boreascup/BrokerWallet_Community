package com.brokerwallet.service;

import com.brokerwallet.dto.PostDTO;
import com.brokerwallet.repository.CommentRepository;
import com.brokerwallet.dto.CommentDTO;
import com.brokerwallet.entity.Comment;
import com.brokerwallet.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    /**
     * 创建评论
     */
    public CommentDTO createComment(CommentDTO commentDTO) {

        Comment comment = new Comment();

        comment.setPostId(commentDTO.getPostId());
        comment.setUserId(commentDTO.getUserId());
        comment.setContent(commentDTO.getContent());
        comment.setCreateTime(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

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
