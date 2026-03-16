package com.brokerwallet.service;

import com.brokerwallet.repository.CommentRepository;
import com.brokerwallet.dto.CommentDTO;
import com.brokerwallet.entity.Comment;
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

        CommentDTO response = new CommentDTO(
                saved.getId(),
                saved.getPostId(),
                saved.getUserId(),
                saved.getContent(),
                saved.getCreateTime()
        );

        return response;
    }

    /**
     * 分页获取评论（升序
     */
    public Page<CommentDTO> getCommentsByPostId(Long postId, Pageable pageable) {

        Page<Comment> commentPage =
                commentRepository.findByPostIdOrderByCreateTimeAsc(postId, pageable);

        return commentPage.map(comment ->
                new CommentDTO(
                        comment.getId(),
                        comment.getPostId(),
                        comment.getUserId(),
                        comment.getContent(),
                        comment.getCreateTime()
                )
        );
    }

    /**
     * 删除评论
     */
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

}
