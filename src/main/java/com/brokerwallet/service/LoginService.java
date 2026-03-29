package com.brokerwallet.service;

import com.brokerwallet.config.JwtProperties;
import com.brokerwallet.dto.UserAccountDTO;
import com.brokerwallet.entity.UserAccount;
import com.brokerwallet.util.EthVerifyKit;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LoginService {

    private final UserAccountService userAccountService;
    private final JwtProperties jwtProperties;

    // 简单内存存储(找时间换成Redis
    private final Map<String, String> nonceStore = new HashMap<>();

    public LoginService(UserAccountService userAccountService, JwtProperties jwtProperties) {
        this.userAccountService = userAccountService;
        this.jwtProperties = jwtProperties;
    }

    // nonce
    public String generateNonce(String walletAddress) {
        String nonce = UUID.randomUUID().toString();
        nonceStore.put(walletAddress, nonce);
        return nonce;
    }

    //登录
    public UserAccountDTO loginBySign(Map<String, String> req) {

        String address = req.get("walletAddress");
        String r = req.get("r");
        String s = req.get("s");
        String v = req.get("v");
        String message = req.get("message");

        // 1. 校验 nonce
        String cacheNonce = nonceStore.get(address);
        if (cacheNonce == null || !cacheNonce.equals(message)) {
            throw new RuntimeException("invalid nonce");
        }

        // 2. 验签
        String recoveredO = EthVerifyKit.recoverAddress(message, r, s, v);
        String recovered = recoveredO.startsWith("0x") ? recoveredO.substring(2) : recoveredO;

        if (!recovered.equalsIgnoreCase(address)) {
            throw new RuntimeException("signature invalid");
        }

        // 3. 删除 nonce（防重放）
        nonceStore.remove(address);

        // 4. 登录 / 注册
        UserAccount user = userAccountService.getOrCreateUser(address);

        // 5. 生成 JWT
        String token = generateJWT(user);

        // 6. 返回 DTO
        UserAccountDTO dto = convertToDTO(user);
        dto.setToken(token);

        return dto;
    }

    // ================= JWT =================
    private String generateJWT(UserAccount user) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + jwtProperties.getExpire() * 1000L);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("walletAddress", user.getWalletAddress())
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


    private UserAccountDTO convertToDTO(UserAccount user) {
        UserAccountDTO dto = new UserAccountDTO();
        dto.setUserId(user.getId());
        dto.setWalletAddress(user.getWalletAddress());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatar());
        return dto;
    }
}