package com.brokerwallet.repository;

import com.brokerwallet.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    boolean existsByPostIdAndFromUserIdAndStatusIn(
            Long postId,
            Long fromUserId,
            Collection<String> statuses
    );
}
