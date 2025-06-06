package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.DefaultStudentDTO;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public Iterable<User> getAll() {
        return userService.getAllUsers();
    }


    @GetMapping("/users/all")
    public List<DefaultStudentDTO> getUserAll() {
        return userService.getAll();
    }

    @DeleteMapping("/users/{userId}")
    public void deleteById(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
