package com.brokerwallet.service;

import com.brokerwallet.common.exception.BizException;
import com.brokerwallet.dto.RewardVerifyRequest;
import com.brokerwallet.entity.Reward;
import com.brokerwallet.repository.RewardRepository;
import com.brokerwallet.util.EthVerifyKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final BlockchainService blockchainService;
    private final RewardRepository rewardRepository;
    private final RewardTxService rewardTxService;



    // 简化：用内存做 nonce 去重（毕设够用）
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
        log.info("解析出的fromAddress:" + recovered);
        // 地址比对
        if (!recovered.equalsIgnoreCase(req.getFrom())) {
            log.info("地址解析错误" );
            return false;
        }
        // 校验 txHash
        boolean txValid = checkTransaction(req.getFrom(), req.getTo(), req.getTimestamp());
        if (!txValid) {
            return false;
        }

        // 到这里说明完全合法
        usedNonce.add(req.getNonce());

        // 8️⃣ 入库（你可以换成 DAO）
        //saveReward(req);

        return true;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    private boolean checkTransaction(String from, String to, Long timeStamp) {
        // 1. 拼接接口地址（acc=from 地址）
        String fromAddress = from.startsWith("0x") ? from.substring(2) : from;
        String toAddress = to.startsWith("0x") ? to.substring(2) : to;
        String apiUrl = "https://dash.broker-chain.com:480/gettx2?acc=" + fromAddress;

        try {
            // 2. 调用接口，获取返回的 Map 结构
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            if (response == null || !response.containsKey("data")) {
                return false;
            }

            // 3. 拿到交易列表 data 数组
            List<Map<String, Object>> txList = (List<Map<String, Object>>) response.get("data");

            // 4. 遍历前10笔交易，匹配 from、to、时间
            int maxCheckCount = 10; // 最多检查前10条
            for (int i = 0; i < txList.size() && i < maxCheckCount; i++) {
                Map<String, Object> tx = txList.get(i);

                // 取出接口返回的字段
                String txFrom = (String) tx.get("from");
                String txTo = (String) tx.get("to");
                String txTime = (String) tx.get("timestamp"); // 格式：2026-03-25T17:08:37+08:00

                // 把接口时间字符串转成毫秒时间戳
                long txTimestamp = convertTimeToTimestamp(txTime);
                log.info("接口返回的txFrom:" + txFrom + " txTo:" + txTo + " txTime:" + txTimestamp);
                log.info("我自己的fromAddress：" + fromAddress + " toAddress:" + toAddress + " txTimestamp:" + timeStamp);

                // 5. 核心匹配：from、to 完全一致 + 时间戳误差 10 秒内（可调整）
                if (fromAddress.equals(txFrom)
                        && toAddress.equals(txTo)
                        && Math.abs(txTimestamp - timeStamp * 1000L) <= 100000000) { // 10秒容错
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

    /**
     * 将 2026-03-25T17:08:37+08:00 转为毫秒时间戳
     */
    private long convertTimeToTimestamp(String timeStr) {
        try {
            return java.time.ZonedDateTime.parse(timeStr).toInstant().toEpochMilli();
        } catch (Exception e) {
            return 0;
        }
    }








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