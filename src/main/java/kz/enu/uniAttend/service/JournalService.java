package kz.enu.uniAttend.service;

import kz.enu.uniAttend.model.DTO.JournalDTO;
import kz.enu.uniAttend.model.DTO.ScheduleDTO;
import kz.enu.uniAttend.model.entity.Journal;
import kz.enu.uniAttend.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {
    private final JournalRepository journalRepository;
    private final ScheduleService scheduleService;

    public List<JournalDTO> getAllJournalBySubjectId(Long subjectId, Long groupId) {
        return journalRepository.findAllByScheduleSubjectIdAndScheduleGroupId(subjectId, groupId).stream().map(this::convertDTO).toList();
    }

    private JournalDTO convertDTO(Journal journal) {
        ScheduleDTO scheduleDTO = scheduleService.convertToScheduleDTO(journal.getSchedule());
        return new JournalDTO(journal.getId(), journal.getUser().getId(), journal.getUser().getEmail(), journal.getUser().getUserName(), journal.getAssessment(), journal.getDateCreate());
    }


//    public class JournalDTO {
//        private Long id;
//        private Long userId;
//        private String email;
//        private String name;
//        private String assessment;
//        private LocalDateTime dateCreate;
//        private ScheduleDTO scheduleDTO;
//    }
}
