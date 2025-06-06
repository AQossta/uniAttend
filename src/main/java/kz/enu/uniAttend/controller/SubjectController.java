package kz.enu.uniAttend.controller;

import kz.enu.uniAttend.model.DTO.SubjectDTO;
import kz.enu.uniAttend.model.request.SubjectRequest;
import kz.enu.uniAttend.repository.SubjectRepository;
import kz.enu.uniAttend.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping("/subject")
    public String addSubject(@RequestBody SubjectRequest subjectRequest) {
        return subjectService.addSubject(subjectRequest.getName());
    }

    @GetMapping("/subject")
    public List<SubjectDTO> getSubjects() {
        return subjectService.getAllSubjects();
    }

    @DeleteMapping("/subject/{subjectId}")
    public String deleteSubject(@PathVariable Long subjectId) {
        return subjectService.deleteSubject(subjectId);
    }
}
