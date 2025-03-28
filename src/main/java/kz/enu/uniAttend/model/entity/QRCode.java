package kz.enu.uniAttend.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_qr_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "qr_code", nullable = false, columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;
}