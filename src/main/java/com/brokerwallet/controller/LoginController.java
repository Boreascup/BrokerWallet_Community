package com.brokerwallet.controller;

import com.brokerwallet.common.result.Result;
import com.brokerwallet.dto.UserAccountDTO;
import com.brokerwallet.service.LoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/nonce")
    public Result<String> getNonce(@RequestParam String walletAddress) {
        return Result.ok(loginService.generateNonce(walletAddress));
    }

    @PostMapping("/sign")
    public Result<UserAccountDTO> loginBySign(@RequestBody Map<String, String> req) {
        return Result.ok(loginService.loginBySign(req));
    }
}