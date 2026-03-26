package com.brokerwallet.service;

import com.brokerwallet.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainService {

    // private final Web3j web3j;
    //
    // /**
    //  * 等待交易确认
    //  */
    // private TransactionReceipt waitForTransactionReceipt(String transactionHash) throws Exception {
    //     log.info("Waiting for transaction receipt: {}", transactionHash);
    //
    //     Optional<TransactionReceipt> receiptOptional = Optional.empty();
    //     int attempts = 0;
    //     int maxAttempts = 40; // 最多等待40次，每次2秒 = 80秒
    //
    //     while (attempts < maxAttempts && !receiptOptional.isPresent()) {
    //         receiptOptional = web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();
    //
    //         if (!receiptOptional.isPresent()) {
    //             Thread.sleep(2000);
    //             attempts++;
    //             log.info("Waiting for transaction confirmation... attempt {}/{}", attempts, maxAttempts);
    //         }
    //     }
    //
    //     if (receiptOptional.isPresent()) {
    //         log.info("Transaction confirmed!");
    //         return receiptOptional.get();
    //     }
    //
    //     throw new RuntimeException("Transaction confirmation timeout");
    // }
    //
    //
    // /**
    //  * 转账原生代币作为奖励（直接转账，不调用合约）
    //  * @param fromAddress 打赏人地址
    //  * @param toAddress 接收地址
    //  * @param amount 转账金额（单位：wei）//1 Token = 10^18 wei
    //  * @return 交易哈希
    //  */
    // public String transferTokenReward(String fromAddress, String toAddress, String amount) throws Exception {
    //     log.info("=== 转账原生代币奖励 ===");
    //
    //     // 标准化地址格式（确保有0x前缀）
    //     if (!fromAddress.startsWith("0x")) {
    //         fromAddress = "0x" + fromAddress;
    //     }
    //     log.info("转账地址: {}", fromAddress);
    //
    //     if (!toAddress.startsWith("0x")) {
    //         toAddress = "0x" + toAddress;
    //     }
    //     log.info("接收地址: {}", toAddress);
    //
    //     // 将金额字符串转换为BigInteger
    //     BigInteger amountInWei = new BigInteger(amount);
    //     log.info("转账金额: {} wei", amountInWei);
    //
    //     // 获取打赏人账户余额
    //     BigInteger userBalance = web3j.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST)
    //             .send().getBalance();
    //     log.info("打赏人账户余额: {} wei ({} Token)", userBalance, userBalance.divide(BigInteger.TEN.pow(18)));
    //
    //     // 检查余额是否充足（包括gas费用）
    //     BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
    //     BigInteger gasLimit = BigInteger.valueOf(21000); // 简单转账的gas limit
    //     BigInteger totalCost = amountInWei.add(gasPrice.multiply(gasLimit));
    //
    //     if (userBalance.compareTo(totalCost) < 0) {
    //         throw new BizException(String.format("账户余额不足: 需要 %s wei（包括gas），但只有 %s wei",
    //                 totalCost, userBalance));
    //     }
    //
    //     // 获取nonce
    //     BigInteger nonce;
    //     try {
    //         EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
    //                 fromAddress, DefaultBlockParameterName.LATEST).send();
    //         nonce = ethGetTransactionCount.getTransactionCount();
    //         log.info("当前nonce: {}", nonce);
    //     } catch (Exception e) {
    //         log.warn("获取nonce失败，使用默认值0: {}", e.getMessage());
    //         nonce = BigInteger.ZERO;
    //     }
    //
    //     log.info("当前gas price: {} wei", gasPrice);
    //
    //     // 创建简单的转账交易（BrokerChain要求必须有Data字段，即使是空值）
    //     Transaction transaction = Transaction.createFunctionCallTransaction(
    //             fromAddress,        // from
    //             nonce,              // nonce
    //             gasPrice,           // gas price
    //             gasLimit,           // gas limit (21000 for simple transfer)
    //             toAddress,          // to
    //             amountInWei,        // value
    //             "0x"                // data (空数据，但必须存在)
    //     );
    //
    //     log.info("交易信息:");
    //     log.info("From: {}", fromAddress);
    //     log.info("To: {}", toAddress);
    //     log.info("Nonce: {}", nonce);
    //     log.info("Gas Price: {}", gasPrice);
    //     log.info("Gas Limit: {}", gasLimit);
    //     log.info("Value: {} wei ({} Token)", amountInWei, amountInWei.divide(BigInteger.TEN.pow(18)));
    //     log.info("Data: 0x (empty but required)");
    //
    //     // 使用 eth_sendTransaction 发送交易（节点会自动签名）
    //     EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).send();
    //
    //     if (ethSendTransaction.hasError()) {
    //         log.error("转账失败: {}", ethSendTransaction.getError().getMessage());
    //         log.error("错误代码: {}", ethSendTransaction.getError().getCode());
    //         throw new RuntimeException("转账失败: " + ethSendTransaction.getError().getMessage());
    //     }
    //
    //     String txHash = ethSendTransaction.getTransactionHash();
    //
    //     if (txHash != null && !txHash.isEmpty()) {
    //         log.info("交易已发送，哈希: {}", txHash);
    //
    //         // 等待交易确认
    //         log.info("等待交易确认: {}", txHash);
    //         TransactionReceipt receipt = waitForTransactionReceipt(txHash);
    //         log.info("交易已确认！");
    //
    //         return txHash;
    //     } else {
    //         throw new RuntimeException("交易发送失败或未返回交易哈希");
    //     }
    // }
}
