package com.brokerwallet.controller;

import com.brokerwallet.common.result.Result;
import com.brokerwallet.dto.PostDTO;
import com.brokerwallet.dto.ProfileHeaderDTO;
import com.brokerwallet.service.PostService;
import com.brokerwallet.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final PostService postService;

    public UserAccountController(UserAccountService userAccountService, PostService postService) {
        this.userAccountService = userAccountService;
        this.postService = postService;
    }

    /**
     * 获取用户信息（用于个人主页）
     */
    @GetMapping("/header/{userId}")
    public Result<ProfileHeaderDTO> getHeader(@PathVariable Long userId) {

        ProfileHeaderDTO dto = userAccountService.getProfileHeader(userId);

        return Result.ok(dto);
    }

    /**
     * 分页获取用户发帖（用于个人主页）
     */
    @GetMapping("/posts/{userId}")
    public Result<Page<PostDTO>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostDTO> result = postService.getUserPosts(userId, pageable);

        return Result.ok(result);
    }


    /**
     * 更新个人信息
     */
    @PostMapping("/update")
    public Result<ProfileHeaderDTO> updateProfile(@RequestBody Map<String, Object> body) {

        Long userId = Long.valueOf(body.get("userId").toString());
        String username = (String) body.get("username");
        String avatar = (String) body.get("avatar");

        ProfileHeaderDTO dto = userAccountService.updateProfileHeader(userId, username, avatar);
        log.info("更新后的用户信息：", dto);

        return Result.ok(dto);
    }
}
