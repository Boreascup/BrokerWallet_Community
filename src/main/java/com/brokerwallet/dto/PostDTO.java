package com.brokerwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private Long id;

    private String avatarUrl;

    private Long userId;

    private String userName;

    private String title;

    private String content;

    private List<String> images;

    private int likeCount;

    private Integer commentCount;

    private BigDecimal rewardAmount;

    private LocalDateTime createTime;

}