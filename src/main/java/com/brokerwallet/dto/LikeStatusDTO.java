package com.brokerwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeStatusDTO {

    private Long postId;

    private Long userId;

    private boolean liked;

    private int likeCount;

}
