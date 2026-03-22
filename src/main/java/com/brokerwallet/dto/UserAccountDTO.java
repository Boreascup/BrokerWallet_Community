package com.brokerwallet.dto;

import lombok.Data;

@Data
public class UserAccountDTO {

    private Long userId;
    private String walletAddress;
    private String username;
    private String avatarUrl;

}