package com.brokerwallet.service;

import com.brokerwallet.config.CosProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sts.v20180813.StsClient;
import com.tencentcloudapi.sts.v20180813.models.GetFederationTokenRequest;
import com.tencentcloudapi.sts.v20180813.models.GetFederationTokenResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class CosTempCredentialService {

    @Resource
    private CosProperties cosProperties;

    /**
     * 获取COS临时密钥（给安卓前端使用）
     */
    public GetFederationTokenResponse getTempCredential() {
        try {
            // 1. 初始化腾讯云认证
            Credential cred = new Credential(
                    cosProperties.getSecretId(),
                    cosProperties.getSecretKey()
            );

            // 2. HTTP配置
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sts.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 3. 初始化STS客户端
            StsClient client = new StsClient(cred, cosProperties.getRegion(), clientProfile);

            // 4. 构造策略（最关键：控制临时密钥权限）
            String policy = "{"
                    + "\"version\":\"2.0\","
                    + "\"statement\":[{"
                    + "\"effect\":\"allow\","
                    + "\"action\":["
                    +   "\"cos:PutObject\","
                    +   "\"cos:GetObject\","
                    +   "\"cos:DeleteObject\""
                    + "],"
                    + "\"resource\":[\"qcs::cos:" + cosProperties.getRegion() + ":uid/" + getAppId() + ":" + cosProperties.getBucketName() + "/*\"]"
                    + "}]"
                    + "}";

            // 5. 请求临时密钥
            GetFederationTokenRequest req = new GetFederationTokenRequest();
            req.setName("brokerwallet_community"); // 自定义名称
            req.setPolicy(policy);
            req.setDurationSeconds(cosProperties.getDurationSeconds()); // 有效期

            // 6. 获取并返回结果
            return client.GetFederationToken(req);
        } catch (Exception e) {
            throw new RuntimeException("获取COS临时密钥失败：" + e.getMessage());
        }
    }

    /**
     * 从存储桶名称中提取 AppId
     * 存储桶格式：bucketName-1250000000  后缀就是AppId
     */
    private String getAppId() {
        String bucket = cosProperties.getBucketName();
        return bucket.substring(bucket.lastIndexOf("-") + 1);
    }
}
