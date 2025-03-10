package kz.enu.uniAttend.model.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDefaultUpdateRequest {
    private Long userId;
    private String userName;
    private String phone;
}
