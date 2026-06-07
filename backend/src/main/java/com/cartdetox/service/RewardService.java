package com.cartdetox.service;

import com.cartdetox.model.*;
import com.cartdetox.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final UserRepository userRepository;
    private final SpinHistoryRepository spinHistoryRepository;

    public List<Reward> getAll() {
        return rewardRepository.findByIsActiveTrue();
    }

    public List<UserReward> getUserRewards(Long userId) {
        return userRewardRepository.findByUserId(userId);
    }

    @Transactional
    public Map<String, Object> completeReward(Long userId, Long rewardId) {
        User user = userRepository.findById(userId).orElseThrow();
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (Boolean.TRUE.equals(reward.getIsRepeatable())) {
            if (userRewardRepository.existsByUserIdAndRewardIdAndCompletedDate(userId, rewardId, LocalDate.now())) {
                throw new RuntimeException("Already completed today");
            }
        } else {
            if (userRewardRepository.countByUserIdAndRewardId(userId, rewardId) > 0) {
                throw new RuntimeException("Already completed");
            }
        }

        UserReward userReward = UserReward.builder()
                .user(user).reward(reward).completedDate(LocalDate.now()).build();
        userRewardRepository.save(userReward);

        user.setDetoxTokens(user.getDetoxTokens() + reward.getTokenReward());
        userRepository.save(user);

        return Map.of(
                "tokensEarned", reward.getTokenReward(),
                "newBalance", user.getDetoxTokens()
        );
    }

    @Transactional
    public Map<String, Object> spin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        if (spinHistoryRepository.existsByUserIdAndSpinDate(userId, LocalDate.now())) {
            throw new RuntimeException("Already spun today");
        }

        int[] prizes = {50, 75, 100, 100, 150, 150, 200, 250, 300, 500};
        int tokensWon = prizes[new Random().nextInt(prizes.length)];

        SpinHistory spin = SpinHistory.builder()
                .user(user).tokensWon(tokensWon).spinDate(LocalDate.now()).build();
        spinHistoryRepository.save(spin);

        user.setLastSpinDate(LocalDate.now());
        user.setDetoxTokens(user.getDetoxTokens() + tokensWon);
        userRepository.save(user);

        return Map.of(
                "tokensWon", tokensWon,
                "newBalance", user.getDetoxTokens()
        );
    }

    public Map<String, Boolean> canSpin(Long userId) {
        boolean available = !spinHistoryRepository.existsByUserIdAndSpinDate(userId, LocalDate.now());
        return Map.of("canSpin", available);
    }
}
