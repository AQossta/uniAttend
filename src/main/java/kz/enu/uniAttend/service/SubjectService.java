package kz.enu.uniAttend.service;

import jakarta.transaction.Transactional;
import kz.enu.uniAttend.model.DTO.SubjectDTO;
import kz.enu.uniAttend.model.entity.Subject;
import kz.enu.uniAttend.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    @Transactional
    public String addSubject(String name) {
        Subject subject = new Subject(name);
        subjectRepository.save(subject);
        return "Предмет успешно сохранен";
    }

    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Предмет не найден"));
    }

    @Transactional
    public String deleteSubject(Long id) {
        subjectRepository.deleteById(id);
        return "Предмет удалено успешно";
    }

    private SubjectDTO convertToDTO(Subject subject) {
        return new SubjectDTO(subject.getId(), subject.getName());
    }
}
