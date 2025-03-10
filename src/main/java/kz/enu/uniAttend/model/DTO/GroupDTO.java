package kz.enu.uniAttend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GroupDTO {
    private Long id;
    private String name;
    private LocalDate dateRegistration;
}
