package kz.enu.uniAttend.service;

import kz.enu.uniAttend.exception.SessionNotFoundException;
import kz.enu.uniAttend.model.entity.Session;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.repository.SessionRepository;
import kz.enu.uniAttend.util.token.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private TokenGenerator tokenGenerator;
    @Autowired
    private UserService userService;
    public Session generateForUser(Long userId) {
        Session session = new Session();
        String token = tokenGenerator.generate();
        session.setToken(token);
        session.setUser(userService.getByUserId(userId));
        session.setExpiration(LocalDateTime.now().plusMinutes(1));
        return sessionRepository.save(session);
    }

    public boolean checkSession(String token) {
        Optional <Session> sessionCheck = sessionRepository.findByToken(token);
        if (sessionCheck.isPresent()) {
            return true;
        } else {
            throw new SessionNotFoundException();
        }
    }

    public User getTokenForUser(String token) {
        Optional<Session> session = sessionRepository.findByToken(token);
        return session.map(Session::getUser)
                .orElseThrow(SessionNotFoundException::new);
    }


    @Transactional
    public boolean invalidate(String token) {
        return sessionRepository.deleteByToken(token) > 0;
    }
    public void manageCountSession(Long userId) {
        List<Session> activeSessions = sessionRepository.findByUserId(userId);
        int maxCountActiveSessions = 2;

        if(activeSessions.size() > maxCountActiveSessions) {
            activeSessions.stream().min(Comparator.comparing(Session::getExpiration)).ifPresent(olderSession -> sessionRepository.delete(olderSession));
        }
    }
}
