package com.brokerwallet.dto;

import lombok.Data;

@Data
public class RewardVerifyRequest {
    private String txHash;
    private String from;
    private String to;
    private Long timestamp;
    private String nonce;
    private String r;
    private String s;
    private String v;
    private String amount;
    private Long postId;
}
