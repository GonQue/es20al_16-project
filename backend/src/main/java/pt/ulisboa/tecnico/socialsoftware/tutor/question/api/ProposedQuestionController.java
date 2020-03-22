package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;

import javax.validation.Valid;

@RestController
public class ProposedQuestionController {
    private QuestionProposalService questionProposalService;

    @PutMapping("/proposedQuestions/{proposedQuestionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#proposedQuestionId, 'PQ.ACCESS')")
    public ProposedQuestionDto teacherEvaluatesProposedQuestion(@PathVariable int proposedQuestionId, @Valid @RequestBody ProposedQuestionDto pqDto){
        return this.questionProposalService.teacherEvaluatesProposedQuestion(pqDto);
    }
}