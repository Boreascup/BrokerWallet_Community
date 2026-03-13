package com.brokerwallet.Repository;
import com.brokerwallet.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户账户数据访问层
 * 提供用户账户的CRUD操作
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    /**
     * 根据钱包地址查询用户
     */
    Optional<UserAccount> findByWalletAddress(String walletAddress);

    /**
     * 判断钱包地址是否已经注册
     */
    boolean existsByWalletAddress(String walletAddress);

    /**
     * 根据显示名称查找用户（模糊匹配）
     */
    List<UserAccount> findByDisplayNameContainingIgnoreCase(String displayName);

    /**
     * 根据显示名称精确查找用户
     */
    Optional<UserAccount> findByDisplayName(String displayName);

}