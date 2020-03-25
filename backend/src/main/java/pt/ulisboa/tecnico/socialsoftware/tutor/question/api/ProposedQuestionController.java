package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;

import javax.validation.Valid;

@RestController
public class ProposedQuestionController {
    @Autowired
    private ProposedQuestionService proposedQuestionService;

    @PutMapping("/proposed-questions/{proposedQuestionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#proposedQuestionId, 'PQ.ACCESS')")
    public ProposedQuestionDto teacherEvaluatesProposedQuestion(@PathVariable Integer proposedQuestionId, @Valid @RequestBody ProposedQuestionDto pqDto){
        return this.proposedQuestionService.teacherEvaluatesProposedQuestion(pqDto);
    }
}