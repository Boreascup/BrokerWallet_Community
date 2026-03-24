package com.brokerwallet.service;

import com.brokerwallet.entity.Post;
import com.brokerwallet.entity.Reward;
import com.brokerwallet.repository.PostRepository;
import com.brokerwallet.repository.RewardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardTxService {

    private final RewardRepository rewardRepository;
    private final PostRepository postRepository;

    /**
     * 阶段1：创建 PENDING
     */
    @Transactional
    public Reward createPendingReward(Long postId,
                                      Long fromUserId,
                                      Long toUserId,
                                      BigDecimal amountBkc) {

        Reward reward = new Reward();
        reward.setPostId(postId);
        reward.setFromUserId(fromUserId);
        reward.setToUserId(toUserId);
        reward.setAmount(amountBkc);
        reward.setStatus("PENDING");
        reward.setCreateTime(LocalDateTime.now());

        return rewardRepository.save(reward);
    }

    /**
     * 阶段3（成功）：更新 SUCCESS
     */
    @Transactional
    public void markSuccess(Long rewardId,
                            String txHash,
                            BigDecimal amountBkc,
                            Long postId) {

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        reward.setStatus("SUCCESS");
        reward.setTxHash(txHash);
        rewardRepository.save(reward);

        // 更新帖子累计打赏
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        BigDecimal current = post.getRewardAmount() == null
                ? BigDecimal.ZERO
                : post.getRewardAmount();

        post.setRewardAmount(current.add(amountBkc));
        postRepository.save(post);
    }

    /**
     * 阶段3（失败）：更新 FAIL
     */
    @Transactional
    public void markFail(Long rewardId, String errorMsg) {

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        reward.setStatus("FAIL");
        reward.setErrorMsg(errorMsg);
        rewardRepository.save(reward);
    }
}
