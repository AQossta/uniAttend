package kz.enu.uniAttend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JournalDTO {
    private Long id;
    private Long userId;
    private String email;
    private String name;
    private String assessment;
    private LocalDateTime dateCreate;
}
