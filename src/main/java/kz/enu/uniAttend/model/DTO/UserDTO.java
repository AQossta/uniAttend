package kz.enu.uniAttend.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private List<String> roles;
    private Long groupId;
    private String groupName;
    private String accessToken;
//    private String organizationName;
//    private String course;
}
