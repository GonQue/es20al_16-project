package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;

import javax.validation.Valid;

@RestController
public class ProposedQuestionController {

    @Autowired
    private QuestionProposalService questionProposalService;

    @PostMapping("/courses/{courseId}/questions/proposed-questions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public ProposedQuestionDto createProposedQuestion(@PathVariable int courseId, @Valid @RequestBody ProposedQuestionDto propQuestionDto) {
        return questionProposalService.studentSubmitQuestion(courseId, propQuestionDto);
    }
}
