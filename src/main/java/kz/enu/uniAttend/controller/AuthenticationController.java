package kz.enu.uniAttend.controller;

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
    public ResponseEntity<MessageResponse<?>> login(@RequestBody LoginRequest loginRequest) {
//        return MessageResponse.of(authenticationService.login(registerRequest.getUserName(), registerRequest.getPassword()));
        Session session = authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String token = session.getToken();
        return ResponseEntity.ok().header("auth-token", token).body(MessageResponse.empty("Успешный вход!!!"));
    }

    //выход
    @PostMapping("/logout")
    public MessageResponse<Boolean> logout(@RequestParam String token) {
        return MessageResponse.of(authenticationService.logout(token));
    }
}
