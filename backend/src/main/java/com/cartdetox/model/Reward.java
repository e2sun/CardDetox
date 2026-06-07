package com.cartdetox.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rewards")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer tokenReward;
    private String category;
    private String icon;

    @Builder.Default
    private Boolean isRepeatable = true;

    @Builder.Default
    private Boolean isActive = true;
}
