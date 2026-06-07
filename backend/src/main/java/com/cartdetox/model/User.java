package com.cartdetox.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private Double detoxTokens = 1000.0;

    @Builder.Default
    private Integer totalOrders = 0;

    @Builder.Default
    private Double totalSaved = 0.0;

    private LocalDate lastSpinDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
