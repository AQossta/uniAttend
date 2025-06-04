package kz.enu.uniAttend.service;

import jakarta.transaction.Transactional;
import kz.enu.uniAttend.exception.UserNotFoundException;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.model.request.user.UserDefaultUpdateRequest;
import kz.enu.uniAttend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final  UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public boolean existsByUsername(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public boolean existsByUserEmail(String userMail) {
        return userRepository.existsByEmail(userMail);
    }

    public User getByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    public User getByUserEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public String updateNameOrNumber(UserDefaultUpdateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(UserNotFoundException::new);
        user.setUserName(request.getUserName());
        user.setPhoneNumber(request.getPhone());
        userRepository.save(user);
        return "Изменение успешно";
    }
}


