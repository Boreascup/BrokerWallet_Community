package com.brokerwallet.controller;

import com.brokerwallet.service.BlockchainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reward")
@Slf4j
public class RewardController {

    private BlockchainService blockchainService;

    /**
     * 管理员转账代币奖励
     */
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferReward(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        log.info("📥 收到转账奖励请求: {}", request);

        try {
            String fromAddress = request.get("fromAddress").toString();
            String toAddress = request.get("toAddress").toString();
            String amount = request.get("amount").toString();

            log.info("🔍 转账奖励: 接收地址={}, 金额={} wei", toAddress, amount);

            // 调用区块链服务转账
            String txHash = blockchainService.transferTokenReward(fromAddress, toAddress, amount);

            log.info("✅ 转账成功: txHash={}", txHash);

            response.put("success", true);
            response.put("message", "转账成功");
            response.put("transactionHash", txHash);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ 转账失败", e);
            response.put("success", false);
            response.put("message", "转账失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
