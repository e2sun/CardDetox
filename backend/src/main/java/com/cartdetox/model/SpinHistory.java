package com.cartdetox.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "spin_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SpinHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer tokensWon;
    private LocalDate spinDate;
}
