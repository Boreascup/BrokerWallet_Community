package com.brokerwallet.service;


import com.brokerwallet.Repository.UserAccountRepository;
import com.brokerwallet.entity.UserAccount;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    /**
     * 根据钱包地址获取或创建用户
     */
    public UserAccount getOrCreateUser(String walletAddress) {

        return userAccountRepository.findByWalletAddress(walletAddress)
                .orElseGet(() -> {
                    UserAccount user = new UserAccount();
                    user.setWalletAddress(walletAddress);
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

    //TODO:更新用户个人信息、用户个人主页发帖查询
}