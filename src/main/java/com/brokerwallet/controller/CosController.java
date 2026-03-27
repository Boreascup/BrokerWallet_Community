package com.brokerwallet.controller;

import com.brokerwallet.service.CosTempCredentialService;
import com.tencentcloudapi.sts.v20180813.models.GetFederationTokenResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cos")
public class CosController {

    @Resource
    private CosTempCredentialService cosTempCredentialService;

    /**
     * 安卓APP请求临时密钥接口
     */
    @GetMapping("/temp-credential")
    public Map<String, Object> getTempCredential() {
        GetFederationTokenResponse response = cosTempCredentialService.getTempCredential();

        Map<String, Object> result = new HashMap<>();
        // 前端必须的三个字段
        result.put("tmpSecretId", response.getCredentials().getTmpSecretId());
        result.put("tmpSecretKey", response.getCredentials().getTmpSecretKey());
        result.put("sessionToken", response.getCredentials().getToken());
        // 过期时间
        result.put("expiredTime", response.getExpiredTime());

        return result;
    }
}