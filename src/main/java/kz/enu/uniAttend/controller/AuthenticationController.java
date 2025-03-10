package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.UserDTO;
import kz.enu.uniAttend.model.entity.Session;
import kz.enu.uniAttend.model.request.LoginRequest;
import kz.enu.uniAttend.model.request.RegisterRequest;
import kz.enu.uniAttend.model.response.MessageResponse;
import kz.enu.uniAttend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    //регистрация
    @PostMapping("/sign-up")
    public MessageResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return MessageResponse.empty(authenticationService.register(registerRequest));
    }


    //логин
    @PostMapping("/sign-in")
    public MessageResponse<UserDTO> login(@RequestBody LoginRequest loginRequest) {
//        return MessageResponse.of(authenticationService.login(registerRequest.getUserName(), registerRequest.getPassword()));
        return MessageResponse.of(authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    //выход
    @PostMapping("/logout")
    public MessageResponse<Boolean> logout(@RequestParam String token) {
        return MessageResponse.of(authenticationService.logout(token));
    }
}
