package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.request.RegisterRequest;
import kz.enu.uniAttend.model.response.MessageResponse;
import kz.enu.uniAttend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    //регистрация
    @PostMapping("/register")
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.register(registerRequest));
    }


    //логин
    @PostMapping("/login")
    public MessageResponse<String> login(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.login(registerRequest));
    }

    //выход
    @PostMapping("/logout")
    public MessageResponse<Boolean> logout(@RequestParam String token) {
        return MessageResponse.of(authenticationService.logout(token));
    }
}
