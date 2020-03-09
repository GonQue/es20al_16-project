package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse;

@Repository
@Transactional
public interface ClarificationResponseRepository extends JpaRepository<ClarificationResponse,Integer> {
    // findById in JpaRepository
}