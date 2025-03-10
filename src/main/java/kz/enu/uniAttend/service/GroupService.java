package kz.enu.uniAttend.service;

import jakarta.transaction.Transactional;
import kz.enu.uniAttend.model.DTO.GroupDTO;
import kz.enu.uniAttend.model.entity.Group;
import kz.enu.uniAttend.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public List<GroupDTO> getAll() {
        return groupRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Group getById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Группа не найдена"));
    }

    @Transactional
    public String create(String name) {
        Group group = new Group();
        group.setName(name);
        groupRepository.save(group);
        return "Группа успешно создано";
    }

    public GroupDTO convertToDTO(Group group) {
        return new GroupDTO(group.getId(), group.getName(), group.getDateRegistration());
    }

    @Transactional
    public String delete(Long id) {
        groupRepository.deleteById(id);
        return "Группа успешно удалена";
    }
}
