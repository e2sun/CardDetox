package com.cartdetox.repository;

import com.cartdetox.model.SpinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface SpinHistoryRepository extends JpaRepository<SpinHistory, Long> {
    boolean existsByUserIdAndSpinDate(Long userId, LocalDate date);
}
