package com.cartdetox.controller;

import com.cartdetox.repository.UserRepository;
import com.cartdetox.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final UserRepository userRepository;

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(rewardService.getAll());
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserRewards(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rewardService.getUserRewards(getUserId(userDetails)));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeReward(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(rewardService.completeReward(getUserId(userDetails), id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/spin")
    public ResponseEntity<?> spin(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(rewardService.spin(getUserId(userDetails)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/spin/can-spin")
    public ResponseEntity<?> canSpin(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rewardService.canSpin(getUserId(userDetails)));
    }
}
