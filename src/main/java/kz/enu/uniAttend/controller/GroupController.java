package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.GroupDTO;
import kz.enu.uniAttend.model.DTO.SubjectDTO;
import kz.enu.uniAttend.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/group")
    public List<GroupDTO> getAllGroup() {
        return groupService.getAll();
    }

    @PostMapping("/group")
    public String addGroup(String name) {
        return groupService.create(name);
    }

    @DeleteMapping("/group")
    public String deleteGroup(Long id) {
        return groupService.delete(id);
    }
}
