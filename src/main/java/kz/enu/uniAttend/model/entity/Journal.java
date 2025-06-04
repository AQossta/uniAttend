package kz.enu.uniAttend.model.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_journal",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "schedule_id"}))
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Пользователь (студент)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Расписание (занятие)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false, length = 10)
    private String assessment = "n";

    @Column(name = "date_create", nullable = false)
    private LocalDateTime dateCreate = LocalDateTime.now();

    public Journal(User user, Schedule schedule, String assessment, LocalDateTime dateCreate) {
        this.user = user;
        this.schedule = schedule;
        this.assessment = assessment;
        this.dateCreate = dateCreate;
    }
}
