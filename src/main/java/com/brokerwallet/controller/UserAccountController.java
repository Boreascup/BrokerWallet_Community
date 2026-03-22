package com.brokerwallet.controller;

import com.brokerwallet.common.result.Result;
import com.brokerwallet.dto.UserAccountDTO;
import com.brokerwallet.entity.UserAccount;
import com.brokerwallet.service.UserAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * 钱包登录（自动注册）
     */
    @PostMapping("/login")
    public Result<UserAccountDTO> login(@RequestBody Map<String, String> request) {

        String walletAddress = request.get("walletAddress");

        if (walletAddress == null || walletAddress.isEmpty()) {
            return Result.fail("walletAddress is empty");
        }

        UserAccount user = userAccountService.getOrCreateUser(walletAddress);
        UserAccountDTO dto = convertToDTO(user);

        return Result.ok(dto);
    }

    /**
     * 获取用户信息（用于个人主页）
     */
    @GetMapping("/{userId}")
    public Result<UserAccountDTO> getUser(@PathVariable Long userId) {

        UserAccount user = userAccountService.findById(userId);

        UserAccountDTO dto = convertToDTO(user);

        return Result.ok(dto);
    }

    // ================= 工具方法 =================

    private UserAccountDTO convertToDTO(UserAccount user) {
        UserAccountDTO dto = new UserAccountDTO();
        dto.setUserId(user.getId());
        dto.setWalletAddress(user.getWalletAddress());
        dto.setUsername(user.getUsername());
        dto.setAvatarUrl(user.getAvatar());
        return dto;
    }
}
