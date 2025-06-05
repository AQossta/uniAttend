package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public Iterable<User> getAll() {
        return userService.getAllUsers();
    }
}
