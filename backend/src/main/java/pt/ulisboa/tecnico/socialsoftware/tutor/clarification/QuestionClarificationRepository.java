package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification;

@Repository
@Transactional
public interface QuestionClarificationRepository extends JpaRepository<QuestionClarification,Integer> {
    // findById in JpaRepository
}
