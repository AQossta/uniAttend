package kz.enu.uniAttend.service;

import jakarta.transaction.Transactional;

import kz.enu.uniAttend.model.entity.Role;
import kz.enu.uniAttend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public void addForUser(Long userId, Long roleId) {
        roleRepository.addForUser(userId, roleId);
    }

    public List<Role> getAllForUserId(Long userId) {
        return roleRepository.getAllForUserId(userId);
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        // Удаляем запись из t_user_roles, которая связывает пользователя с ролью
        roleRepository.deleteRoleFromUser(userId, roleId);
    }
}
