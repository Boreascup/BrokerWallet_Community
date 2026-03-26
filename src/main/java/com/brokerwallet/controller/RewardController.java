package com.brokerwallet.controller;

import com.brokerwallet.common.result.Result;
import com.brokerwallet.dto.RewardVerifyRequest;
import com.brokerwallet.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reward")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @PostMapping("/verify")
    public Result<Boolean> verify(@RequestBody RewardVerifyRequest req) {
        try {
            boolean result = rewardService.verifyAndSave(req);
            return Result.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.ok(false);
        }
    }
}
