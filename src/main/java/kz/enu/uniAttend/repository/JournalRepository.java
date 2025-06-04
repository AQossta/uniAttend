package kz.enu.uniAttend.repository;

import kz.enu.uniAttend.model.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByScheduleGroupId(Long groupId);

    List<Journal> findAllByScheduleSubjectId(Long subjectId);

    List<Journal> findAllByScheduleSubjectIdAndScheduleGroupId(Long subjectId, Long groupId);
}
