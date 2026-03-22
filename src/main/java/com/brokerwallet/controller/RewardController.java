package com.brokerwallet.controller;

import com.brokerwallet.common.result.Result;
import com.brokerwallet.dto.RewardDTO;
import com.brokerwallet.service.RewardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reward")
@Slf4j
public class RewardController {

    @Autowired
    private RewardService rewardService;

    /**
     * 打赏转账(单位：BKC
     */
    @PostMapping("/transfer")
    public Result<String> transferReward(
            @RequestBody RewardDTO request) {

        Map<String, Object> response = new HashMap<>();

        log.info("📥 收到转账奖励请求: {}", request);

        Long postId = request.getPostId();
        Long fromUserId = request.getFromUserId();
        Long toUserId = request.getToUserId();
        String fromAddress = request.getFromAddress();
        String toAddress = request.getToAddress();
        BigDecimal amountBkc = request.getAmount();

        log.info("🔍 转账奖励: 帖子ID={}, 转账地址={}, 接收地址={}, 金额={} BKC",
                postId, fromAddress, toAddress, amountBkc);

        String txHash = rewardService.rewardPost(
                postId,
                fromUserId,
                toUserId,
                fromAddress,
                toAddress,
                amountBkc
        );

        response.put("success", true);
        response.put("transactionHash", txHash);

        return Result.ok(txHash);
    }
}
