package com.cartdetox.repository;

import com.cartdetox.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByIsActiveTrue();
    boolean existsByTitle(String title);
}
