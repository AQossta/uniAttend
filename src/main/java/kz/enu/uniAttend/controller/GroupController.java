package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.DefaultStudentDTO;
import kz.enu.uniAttend.model.DTO.GroupDTO;
import kz.enu.uniAttend.model.entity.Role;
import kz.enu.uniAttend.model.entity.User;
import kz.enu.uniAttend.model.request.GroupRequest;
import kz.enu.uniAttend.repository.UserRepository;
import kz.enu.uniAttend.service.GroupService;
import kz.enu.uniAttend.service.RoleService;
import kz.enu.uniAttend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @GetMapping("/group")
    public List<GroupDTO> getAllGroup() {
        return groupService.getAll();
    }

    @PostMapping("/group")
    public String addGroup(@RequestBody GroupRequest request) {
        return groupService.create(request.getName());
    }

    @DeleteMapping("/group")
    public String deleteGroup(Long id) {
        return groupService.delete(id);
    }

    @GetMapping("/group/{groupId}")
    public List<DefaultStudentDTO> getAllUserGroupId(@PathVariable Long groupId) {
        return userRepository.findByGroupId(groupId).stream().map(this::convertToDefaultStudentDTO).toList();
    }

    private DefaultStudentDTO convertToDefaultStudentDTO(User user){
        List<Role> roles = roleService.getAllForUserId(user.getId());
        List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());


        return new DefaultStudentDTO(user.getId(), user.getUserName(), user.getEmail(), user.getPhoneNumber(), user.getBirthday(), roleNames);
    }

}
