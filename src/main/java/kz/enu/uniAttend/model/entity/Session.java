package kz.enu.uniAttend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_sessions")
@Data
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}