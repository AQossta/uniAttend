package kz.enu.uniAttend.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_invites")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "link", nullable = false, length = 255)
    private String link;

    @Column(name = "date_create", nullable = false)
    private LocalDateTime dateCreate = LocalDateTime.now();

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
}
