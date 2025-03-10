package kz.enu.uniAttend.service;

import kz.enu.uniAttend.exception.UserNotFoundException;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
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
}
