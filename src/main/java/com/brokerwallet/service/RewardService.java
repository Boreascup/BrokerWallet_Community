package com.brokerwallet.service;

import com.brokerwallet.dto.RewardVerifyRequest;
import com.brokerwallet.entity.Reward;
import com.brokerwallet.entity.UserAccount;
import com.brokerwallet.repository.PostRepository;
import com.brokerwallet.repository.RewardRepository;
import com.brokerwallet.repository.UserAccountRepository;
import com.brokerwallet.util.EthVerifyKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final UserAccountRepository userAccountRepository;
    private final RewardRepository rewardRepository;
    private final PostRepository postRepository;

    private static final String DASHBOARD_URL = "https://dash.broker-chain.com:480/gettx2?acc=";

    // 简化：用内存做 nonce 去重
    private final Set<String> usedNonce = ConcurrentHashMap.newKeySet();

    public boolean verifyAndSave(RewardVerifyRequest req) {

        // 基础参数校验
        if (req.getTxHash() == null || req.getFrom() == null) {
            return false;
        }
        // 防重放（nonce）
        if (usedNonce.contains(req.getNonce())) {
            return false;
        }
        // 时间戳校验（5分钟）
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - req.getTimestamp()) > 300) {
            return false;
        }
        // 重建 message
        String message = req.getTxHash() + "|" +
                req.getFrom() + "|" +
                req.getTo() + "|" +
                req.getTimestamp() + "|" +
                req.getNonce();
        // 验签 → 恢复地址
        String recovered = EthVerifyKit.recoverAddress(
                message,
                req.getR(),
                req.getS(),
                req.getV()
        );
        // 地址比对
        if (!recovered.equalsIgnoreCase(req.getFrom())) {
            log.info("地址解析错误" );
            return false;
        }
        // 通过打赏地址+受赏地址+时间戳，调用dashboard接口查询是否存在该笔转账
        boolean txValid = checkTransaction(req.getFrom(), req.getTo(), req.getTimestamp());
        if (!txValid) {
            return false;
        }
        //前面都过了说明无误
        usedNonce.add(req.getNonce());

        // 数据入库
        saveReward(req);

        return true;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    // 调用dashboard接口寻找交易
    private boolean checkTransaction(String from, String to, Long timeStamp) {
        // 1. 拼接接口地址（acc=from 地址）
        String fromAddress = from.startsWith("0x") ? from.substring(2) : from;
        String toAddress = to.startsWith("0x") ? to.substring(2) : to;
        String apiUrl = DASHBOARD_URL + fromAddress;

        try {
            // 2. 调用dashboard接口
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response == null || !response.containsKey("data")) {
                return false;
            }

            // 3. 拿到交易列表 data 数组
            List<Map<String, Object>> txList = (List<Map<String, Object>>) response.get("data");

            // 4. 遍历前5笔交易，匹配 from、to、时间
            int maxCheckCount = 5;
            for (int i = 0; i < txList.size() && i < maxCheckCount; i++) {
                Map<String, Object> tx = txList.get(i);

                String txFrom = (String) tx.get("from");
                String txTo = (String) tx.get("to");
                String txTime = (String) tx.get("timestamp");
                long txTimestamp = convertTimeToTimestamp(txTime);
                log.info("接口返回的txFrom:" + txFrom + " txTo:" + txTo + " txTime:" + txTimestamp);
                log.info("本次打赏的fromAddress：" + fromAddress + " toAddress:" + toAddress + " txTimestamp:" + timeStamp);

                // 5. from、to 完全一致 + 时间戳误差 10 秒内
                if (fromAddress.equals(txFrom)
                        && toAddress.equals(txTo)
                        && Math.abs(txTimestamp - timeStamp) <= 10000) { // 10秒容错
                    log.info("成功找到交易");
                    return true;
                }
            }

        } catch (Exception e) {
            System.err.println("查询交易异常：" + e.getMessage());
        }

        // 没找到匹配交易
        log.info("没有找到交易");
        return false;
    }

    //转为秒级时间戳
    private long convertTimeToTimestamp(String timeStr) {
        try {
            return java.time.ZonedDateTime.parse(timeStr).toInstant().getEpochSecond();
        } catch (Exception e) {
            return 0;
        }
    }

    //数据入库
    public void saveReward(RewardVerifyRequest request) {

        String from = request.getFrom();
        String fromAddress = from.startsWith("0x") ? from.substring(2) : from;

        UserAccount fromUser = userAccountRepository
                .findByWalletAddress(fromAddress)
                .orElseThrow(() -> new RuntimeException("发送人不存在"));

        UserAccount toUser = userAccountRepository
                .findByWalletAddress(request.getTo())
                .orElseThrow(() -> new RuntimeException("接收人不存在"));

        BigDecimal amount = new BigDecimal(request.getAmount());

        // 1. 更新 post
        int updated = postRepository.addRewardAmount(request.getPostId(), amount);
        if (updated == 0) {
            throw new RuntimeException("帖子不存在");
        }

        // 2. 插入 reward
        Reward reward = new Reward();
        reward.setPostId(request.getPostId());
        reward.setFromUserId(fromUser.getId());
        reward.setToUserId(toUser.getId());
        reward.setAmount(amount);
        reward.setTxHash(request.getTxHash());
        reward.setStatus("SUCCESS");
        reward.setCreateTime(LocalDateTime.now());

        rewardRepository.save(reward);
    }
}