package com.brokerwallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RewardDTO {

    private Long postId;

    private Long fromUserId;

    private Long toUserId;

    private String fromAddress;

    private String toAddress;

    private BigDecimal amount; // BKC
}
