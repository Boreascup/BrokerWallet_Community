package com.brokerwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private int likeCount;

    private LocalDateTime createTime;

}