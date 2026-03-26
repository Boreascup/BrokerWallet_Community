package com.brokerwallet.service;


import com.brokerwallet.dto.ProfileHeaderDTO;
import com.brokerwallet.repository.UserAccountRepository;
import com.brokerwallet.entity.UserAccount;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    /**
     * 根据钱包地址获取或创建用户
     */
    public UserAccount getOrCreateUser(String walletAddress) {

        return userAccountRepository.findByWalletAddress(walletAddress)
                .orElseGet(() -> {
                    UserAccount user = new UserAccount();
                    user.setWalletAddress(walletAddress);
                    // 默认信息
                    user.setUsername("用户" + System.currentTimeMillis() % 10000);
                    user.setAvatar("https://default-avatar.png");

                    return userAccountRepository.save(user);
                });
    }

    /**
     * 根据钱包地址查找用户
     */
    public UserAccount findByWalletAddress(String walletAddress) {
        return userAccountRepository
                .findByWalletAddress(walletAddress)
                .orElse(null);
    }

    /**
     * 根据ID查找用户
     */
    public UserAccount findById(Long id) {
        return userAccountRepository
                .findById(id)
                .orElse(null);
    }

    /**
     * 保存用户
     */
    public UserAccount save(UserAccount user) {
        return userAccountRepository.save(user);
    }


    public ProfileHeaderDTO getProfileHeader(Long userId) {

        ProfileHeaderDTO dto = userAccountRepository.getProfileHeader(userId);
        dto.setRewardTotal(
                dto.getRewardTotal() == null
                        ? BigDecimal.ZERO
                        : dto.getRewardTotal().stripTrailingZeros()
        );
        return dto;
    }

    //TODO:更新用户个人信息
}