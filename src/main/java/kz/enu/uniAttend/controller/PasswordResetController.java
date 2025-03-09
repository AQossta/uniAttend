package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.request.PasswordResetRequest;
import kz.enu.uniAttend.model.response.MessageResponse;
import kz.enu.uniAttend.service.PasswordResetService;
import kz.enu.uniAttend.service.SessionService;
import kz.enu.uniAttend.service.UserService;
import kz.enu.uniAttend.util.encoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/password")
public class PasswordResetController {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetService passwordResetService;

    @PutMapping("/editUser")
    public MessageResponse<String> editUser(@RequestBody PasswordResetRequest passwordResetRequest) {
        return MessageResponse.empty(passwordResetService.editUser(passwordResetRequest));
    }
}
