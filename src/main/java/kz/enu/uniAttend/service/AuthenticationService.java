package kz.enu.uniAttend.service;

import kz.enu.uniAttend.exception.AuthenticationErrorException;
import kz.enu.uniAttend.exception.InvalidPasswordException;
import kz.enu.uniAttend.exception.UserAlreadyExistsException;
import kz.enu.uniAttend.model.entity.Group;
import kz.enu.uniAttend.model.entity.Role;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.model.request.RegisterRequest;
import kz.enu.uniAttend.repository.GroupRepository;
import kz.enu.uniAttend.repository.RoleRepository;
import kz.enu.uniAttend.repository.UserRepository;
import kz.enu.uniAttend.util.encoder.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final RoleService roleService;
    private final GroupService groupService;

    public String register(RegisterRequest registerRequest) {
        if(userService.existsByUserEmail(registerRequest.getName())) {
            throw new UserAlreadyExistsException();
        } else {
            User user = new User(registerRequest.getEmail(),
                    registerRequest.getName(),
                    passwordEncoder.hash(registerRequest.getPassword()),
                    registerRequest.getPhoneNumber(),
                    registerRequest.getBirthday(),
                    groupService.getById(registerRequest.getGroupId())
            );
//            (String email, String name, String password, String phoneNumber, LocalDate birthday, Group group, Role role)
            userService.saveUser(user);
            return "Пользователь успешно создан";
        }
    }

    public String login(RegisterRequest registerRequest) {
        if(userService.existsByUserEmail(registerRequest.getEmail())) {
            User user = userService.getByUserName(registerRequest.getName());
            String passwordMatches = user.getPassword();
            Long userId = user.getId();
            if(passwordEncoder.check(registerRequest.getPassword(), passwordMatches)) {
                sessionService.manageCountSession(userId);
                return sessionService.generateForUser(userId);
            } else {
                throw new InvalidPasswordException();
            }
        } else {
            throw new AuthenticationErrorException();
        }
    }

    public boolean logout(String token)  {
        return sessionService.invalidate(token);
    }
}

