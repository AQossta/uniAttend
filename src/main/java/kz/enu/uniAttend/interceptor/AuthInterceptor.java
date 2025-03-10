package kz.enu.uniAttend.interceptor;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.enu.uniAttend.model.entity.Role;
import kz.enu.uniAttend.model.entity.Session;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.repository.RoleRepository;
import kz.enu.uniAttend.service.SessionService;
import kz.enu.uniAttend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;
    private final RoleRepository roleRepository;

    @Autowired
    public AuthInterceptor(RoleRepository roleRepository, SessionService sessionService) {
        this.roleRepository = roleRepository;
        this.sessionService = sessionService;
    }


    //roles
    @Value("${userRoles.admin}")
    private String ROLE_ADMIN;

    @Value("${userRoles.user}")
    private String ROLE_USER;

    @Value("${userRoles.teacher}")
    private String ROLE_TEACHER;

    @Value("${userRoles.student}")
    private String ROLE_STUDENT;

    //api
    @Value("${api.path.admin}")
    private String PATH_ADMIN_API;

    @Value("${api.path.user}")
    private String PATH_USER_API;

    @Value("${api.path.teacher}")
    private String PATH_TEACHER_API;

    @Value("${api.path.student}")
    private String PATH_STUDENT_API;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String authToken = request.getHeader("auth-token");

        if (authToken != null && !authToken.isEmpty()) {
            Session sessionOpt = validateSession(authToken);
            User user = sessionOpt.getUser();
            List<Role> roles = roleRepository.getAllForUserId(user.getId());
            request.setAttribute("user", user);
            request.setAttribute("roles", roles);

            if (hasAccess(request.getRequestURI(), roles)) {
                System.out.println(request.getRequestURI());
                return true;
            }
            sendAccessDeniedResponse(response, "Доступ запрещен: недостаточно прав");
            return false;
        }
        sendAccessDeniedResponse(response, "Доступ запрещен: недействительный токен");
        return false;
    }

    private Session validateSession(String authToken) {
        Session session = sessionService.getSessionByToken(authToken);
        if (LocalDateTime.now().isAfter(session.getExpiration())) {
            sessionService.invalidate(session.getToken());
            throw new RuntimeException("Срок действия сессии истек. Пожалуйста, войдите в систему снова.");
            }
        return session;
    }

    private final Map<String, String> rolePathMap = new HashMap<>();

    @PostConstruct
    private void initRolePathMap() {
        if (PATH_ADMIN_API != null && ROLE_ADMIN != null) {
            rolePathMap.put(PATH_ADMIN_API, ROLE_ADMIN);
        }
        if (PATH_USER_API != null && ROLE_USER != null) {
            rolePathMap.put(PATH_USER_API, ROLE_USER);
        }
        if (PATH_TEACHER_API != null && ROLE_TEACHER != null) {
            rolePathMap.put(PATH_TEACHER_API, ROLE_TEACHER);
        }
        if (PATH_STUDENT_API != null && ROLE_STUDENT != null) {
            rolePathMap.put(PATH_STUDENT_API, ROLE_STUDENT);
        }

        System.out.println("Initialized rolePathMap: " + rolePathMap);
    }

    private boolean hasAccess(String requestPath, List<Role> roles) {
        // Если путь не начинается с тех путей, которые требуют проверки, разрешаем доступ
        if (!requestPath.startsWith(PATH_ADMIN_API)
                && !requestPath.startsWith(PATH_USER_API)
                && !requestPath.startsWith(PATH_TEACHER_API)
                && !requestPath.startsWith(PATH_STUDENT_API)) {
            return true;  // Пропускаем проверку и разрешаем доступ
        }

        // Проверяем доступ для различных путей
        Set<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toSet());

        // Проверка для пути admin
        if (requestPath.startsWith(PATH_ADMIN_API) && roleNames.contains(ROLE_ADMIN)) {
            return true;
        }

        // Проверка для пути user
        if (requestPath.startsWith(PATH_USER_API) && roleNames.contains(ROLE_USER)) {
            return true;
        }

        // Проверка для пути teacher
        if (requestPath.startsWith(PATH_TEACHER_API) && roleNames.contains(ROLE_TEACHER)) {
            return true;
        }

        // Проверка для пути student
        if (requestPath.startsWith(PATH_STUDENT_API) && roleNames.contains(ROLE_STUDENT)) {
            return true;
        }

        return false;
    }

    private void sendAccessDeniedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}