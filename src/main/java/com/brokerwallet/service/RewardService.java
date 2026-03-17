package com.brokerwallet.service;

import com.brokerwallet.common.exception.BizException;
import com.brokerwallet.entity.Reward;
import com.brokerwallet.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final BlockchainService blockchainService;
    private final RewardRepository rewardRepository;
    private final RewardTxService rewardTxService;

    /**
     * 打赏主流程
     * @param postId 帖子ID
     * @param fromUserId 打赏人ID
     * @param toUserId 受赏人ID
     * @param fromAddress 打赏人地址
     * @param toAddress 受赏人地址
     * @param amountBkc 打赏金额（单位:BKC
     * @return 交易哈希
     */
    public String rewardPost(Long postId,
                             Long fromUserId,
                             Long toUserId,
                             String fromAddress,
                             String toAddress,
                             BigDecimal amountBkc) {

        boolean exists = rewardRepository
                .existsByPostIdAndFromUserIdAndStatusIn(
                        postId,
                        fromUserId,
                        List.of("PENDING", "SUCCESS")
                );
        if (exists) {
            throw new BizException("请勿重复打赏");
        }

        // 创建 PENDING 记录
        Reward reward = rewardTxService.createPendingReward(
                postId, fromUserId, toUserId, amountBkc
        );

        try {
            // 调用区块链
            BigInteger wei = amountBkc
                    .multiply(BigDecimal.TEN.pow(18))
                    .toBigInteger();
            String txHash = blockchainService.transferTokenReward(
                    fromAddress,
                    toAddress,
                    wei.toString()
            );

            // 标记成功
            rewardTxService.markSuccess(reward.getId(), txHash, amountBkc, postId);
            return txHash;

        } catch (BizException e) {

            // 业务异常（如余额不足）
            rewardTxService.markFail(reward.getId(), e.getMessage());
            throw e;

        } catch (Exception e) {

            // 系统异常
            rewardTxService.markFail(reward.getId(), "系统异常");
            throw new BizException("转账失败，请稍后重试");
        }
    }
}