package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion;

import java.util.List;

@Repository
@Transactional
public interface ClarificationQuestionRepository extends JpaRepository<ClarificationQuestion,Integer> {
    // findById in JpaRepository

    @Query(value = "select * from clarifications where available_to_other_students = 't' and question_id = :questionId ;", nativeQuery = true)
    List<ClarificationQuestion> findOtherPublicClarificationQuestions(Integer questionId);
}