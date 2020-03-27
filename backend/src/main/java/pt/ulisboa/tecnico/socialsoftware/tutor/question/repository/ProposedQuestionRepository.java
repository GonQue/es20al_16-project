package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;


@Repository
@Transactional
public interface ProposedQuestionRepository extends JpaRepository<ProposedQuestion, Integer> {}