package com.brokerwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProfileHeaderDTO {

    private Long userId;

    private String username;

    private String avatar;

    private Long postCount;

    private BigDecimal rewardTotal;

    public ProfileHeaderDTO(Long userId,
                            String username,
                            String avatar,
                            Long postCount,
                            BigDecimal rewardTotal) {
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.postCount = postCount;
        this.rewardTotal = rewardTotal != null ? rewardTotal : BigDecimal.ZERO;
    }
}
