package com.brokerwallet.util;


import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * 验签工具类
 * 功能：从 r + s + v + 原始数据 恢复公钥 → 生成地址
 */
public class EthVerifyKit {

    public static String recoverAddress(String message, String rHex, String sHex, String vHex) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        int vInt = Numeric.toBigInt(vHex).intValueExact();
        byte v = (byte) vInt;

        byte[] r = Numeric.hexStringToByteArray(rHex);
        byte[] s = Numeric.hexStringToByteArray(sHex);
        r = Numeric.toBytesPadded(new BigInteger(1, r), 32);
        s = Numeric.toBytesPadded(new BigInteger(1, s), 32);

        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);

        try {
            BigInteger publicKey = Sign.signedPrefixedMessageToKey(messageBytes, signatureData);

            byte[] publicKeyBytes = Numeric.toBytesPadded(publicKey, 64);

            Keccak.Digest256 keccak = new Keccak.Digest256();
            keccak.update(publicKeyBytes);
            byte[] hash = keccak.digest();

            byte[] addr = new byte[20];
            System.arraycopy(hash, 12, addr, 0, 20);

            return "0x" + Hex.toHexString(addr).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("还原失败", e);
        }
    }
}
