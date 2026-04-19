-- =====================
-- 用户表
-- =====================
CREATE TABLE IF NOT EXISTS user_account (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            wallet_address VARCHAR(42) NOT NULL UNIQUE COMMENT '钱包地址',
    username VARCHAR(100) COMMENT '用户名',
    avatar VARCHAR(255) COMMENT '头像',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_wallet_address ON user_account (wallet_address);


-- =====================
-- 帖子表
-- =====================
CREATE TABLE IF NOT EXISTS post (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
                                    user_id BIGINT NOT NULL COMMENT '发帖用户',
                                    title VARCHAR(255) COMMENT '标题',
    content TEXT COMMENT '帖子内容',
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    reward_amount DECIMAL(38,8) DEFAULT 0,
    images VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_user
    FOREIGN KEY (user_id) REFERENCES user_account(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_post_user ON post (user_id);
CREATE INDEX idx_post_create_time ON post (create_time);


-- =====================
-- 评论表
-- =====================
CREATE TABLE IF NOT EXISTS comment (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
                                       post_id BIGINT NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       content TEXT NOT NULL,
                                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_comment_post
                                       FOREIGN KEY (post_id) REFERENCES post(id),
    CONSTRAINT fk_comment_user
    FOREIGN KEY (user_id) REFERENCES user_account(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_comment_post ON comment (post_id);
CREATE INDEX idx_comment_user ON comment (user_id);


-- =====================
-- 点赞表
-- =====================
CREATE TABLE IF NOT EXISTS post_like (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         post_id BIGINT NOT NULL,
                                         user_id BIGINT NOT NULL,
                                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT uk_post_user UNIQUE (post_id, user_id),

    CONSTRAINT fk_like_post
    FOREIGN KEY (post_id) REFERENCES post(id),
    CONSTRAINT fk_like_user
    FOREIGN KEY (user_id) REFERENCES user_account(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_like_post ON post_like (post_id);


-- =====================
-- 打赏表
-- =====================
CREATE TABLE IF NOT EXISTS reward (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      post_id BIGINT NOT NULL,
                                      from_user_id BIGINT NOT NULL,
                                      to_user_id BIGINT NOT NULL,
                                      amount DECIMAL(38,8),
    tx_hash VARCHAR(80),
    status VARCHAR(20) DEFAULT 'PENDING',
    error_msg VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reward_post
    FOREIGN KEY (post_id) REFERENCES post(id),
    CONSTRAINT fk_reward_from_user
    FOREIGN KEY (from_user_id) REFERENCES user_account(id),
    CONSTRAINT fk_reward_to_user
    FOREIGN KEY (to_user_id) REFERENCES user_account(id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_reward_post ON reward (post_id);
CREATE INDEX idx_reward_user ON reward (from_user_id);