package com.cartdetox.repository;

import com.cartdetox.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findByUserId(Long userId);
    boolean existsByUserIdAndRewardIdAndCompletedDate(Long userId, Long rewardId, LocalDate date);
    long countByUserIdAndRewardId(Long userId, Long rewardId);
}
