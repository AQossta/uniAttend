package kz.enu.uniAttend.service;

import kz.enu.uniAttend.model.entity.Group;
import kz.enu.uniAttend.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    public Group getById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Группа не найдена"));
    }
}
