package com.brokerwallet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward")
@Data
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "from_user_id")
    private Long fromUserId;

    @Column(name = "to_user_id")
    private Long toUserId;

    @Column(name = "amount", precision = 38, scale = 8)
    private BigDecimal amount; // BKC

    @Column(name = "tx_hash", unique = true)
    private String txHash;

    @Column(name = "status")
    private String status;

    @Column(name = "error_msg")
    private String errorMsg;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
