package com.brokerwallet.util;

import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * secp256k1 验签工具类
 * 功能：从 r + s + v + 原始数据 恢复公钥 → 生成地址
 */
public class EthVerifyKit {

    // secp256k1 曲线参数（全局单例）
    private static final ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
    private static final ECDomainParameters domain = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN());
    private static final BigInteger CURVE_N = spec.getN();

    /**
     * 从签名恢复公钥（
     * @param r 签名r
     * @param s 签名s
     * @param v 签名v(recoveryId)
     * @param data 原始数据
     * @return 恢复出的公钥(04开头，非压缩格式)
     */
    public static String recoverPublicKey(String r, String s, int v, String data) {
        try {
            // 1. 原始数据做 Keccak256（必须和前端一致）
            byte[] dataBytes = data.getBytes();
            KeccakDigest keccak = new KeccakDigest(256);
            keccak.update(dataBytes, 0, dataBytes.length);
            byte[] hash = new byte[32];
            keccak.doFinal(hash, 0);

            // 2. 转换r、s为BigInteger
            BigInteger rBi = new BigInteger(r, 16);
            BigInteger sBi = new BigInteger(s, 16);

            // 3. 核心：恢复公钥
            ECPoint publicPoint = recoverPubKey(rBi, sBi, hash, v);
            if (publicPoint == null) {
                return null;
            }

            // 4. 返回非压缩公钥（04开头，和前端格式一致）
            return Hex.toHexString(publicPoint.getEncoded(false));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 公钥生成地址（和你前端 GetAddress 完全一致）
     */
    public static String getAddressFromPublicKey(String publicKey) {
        try {
            byte[] pubKeyBytes = Hex.decode(publicKey);
            KeccakDigest keccakDigest = new KeccakDigest(256);
            // 跳过第一个字节 0x04
            keccakDigest.update(pubKeyBytes, 1, pubKeyBytes.length - 1);
            byte[] hash = new byte[32];
            keccakDigest.doFinal(hash, 0);

            // 取后20字节
            byte[] address = new byte[20];
            System.arraycopy(hash, 12, address, 0, 20);
            return Hex.toHexString(address);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==============================================
    // 内部核心：公钥恢复算法（固定标准，无需修改）
    // ==============================================
    private static ECPoint recoverPubKey(BigInteger r, BigInteger s, byte[] hash, int v) {
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(CURVE_N) >= 0) {
            return null;
        }

        BigInteger i = BigInteger.valueOf(v / 2);
        BigInteger x = r.add(i.multiply(CURVE_N));
        ECPoint R = decompressKey(x, (v & 1) == 1);
        if (R == null || !R.multiply(CURVE_N).isInfinity()) {
            return null;
        }

        BigInteger e = new BigInteger(1, hash);
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(CURVE_N);
        BigInteger rInv = r.modInverse(CURVE_N);
        BigInteger srInv = rInv.multiply(s).mod(CURVE_N);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(CURVE_N);

        ECPoint q = domain.getG().multiply(eInvrInv).add(R.multiply(srInv));
        return q.normalize();
    }

    private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        try {
            byte[] xEnc = bigIntegerToBytes(xBN, 32);
            byte prefix = (byte) (yBit ? 0x03 : 0x02);
            return spec.getCurve().decodePoint(concatenate(prefix, xEnc));
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] bigIntegerToBytes(BigInteger bi, int length) {
        byte[] bytes = bi.toByteArray();
        byte[] result = new byte[length];
        int srcPos = (bytes.length > 0 && bytes[0] == 0) ? 1 : 0;
        int destPos = length - Math.min(bytes.length - srcPos, length);
        System.arraycopy(bytes, srcPos, result, destPos, bytes.length - srcPos);
        return result;
    }

    private static byte[] concatenate(byte b, byte[] array) {
        byte[] res = new byte[array.length + 1];
        res[0] = b;
        System.arraycopy(array, 0, res, 1, array.length);
        return res;
    }
}
