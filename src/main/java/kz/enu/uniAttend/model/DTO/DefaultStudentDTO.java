package kz.enu.uniAttend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class DefaultStudentDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private List<String> roles;
}
