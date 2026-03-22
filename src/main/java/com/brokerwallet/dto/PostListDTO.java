package com.brokerwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListDTO {
    private Long postId;
    private String title;
    private String content;
    private String username;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createTime;
}
