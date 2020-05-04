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

    @Query(value = "select * from clarifications where available_to_other_students = 't' and user_id != :studentId and question_id in ( select question_id from quiz_questions where id in ( select quiz_question_id from question_answers where quiz_answer_id in ( select id from quiz_answers where user_id = :studentId) ) ) ;", nativeQuery = true)
    List<ClarificationQuestion> findOtherPublicClarificationQuestions(Integer studentId);
}