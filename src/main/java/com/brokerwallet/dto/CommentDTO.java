package com.brokerwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long id;

    private Long postId;

    private Long userId;

    private String userName;

    private String content;

    private LocalDateTime createTime;

}
