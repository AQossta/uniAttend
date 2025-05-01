package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.GroupDTO;
import kz.enu.uniAttend.model.request.GroupRequest;
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
    public String addGroup(@RequestBody GroupRequest request) {
        return groupService.create(request.getName());
    }

    @DeleteMapping("/group")
    public String deleteGroup(Long id) {
        return groupService.delete(id);
    }
}
