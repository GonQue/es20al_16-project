package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class ClarificationController {

    @Autowired
    private ClarificationService clarificationService;

    @PostMapping("/clarifications/{questionId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ClarificationQuestionDto createClarificationQuestion(Principal principal, @PathVariable Integer questionId, @RequestBody ClarificationQuestionDto clarificationQuestionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.createClarification(questionId, user.getId(), clarificationQuestionDto);
    }

    @PostMapping("clarifications/{clarificationId}/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ClarificationResponseDto createClarificationResponse(Principal principal, @PathVariable Integer clarificationId, @RequestBody ClarificationResponseDto clarificationResponseDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return clarificationService.answerClarification(clarificationId, user.getId(), clarificationResponseDto);
    }

    @GetMapping("clarifications/{clarificationId}/responses")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<ClarificationResponseDto> listResponses(@PathVariable Integer clarificationId) {
        return clarificationService.listResponses(clarificationId);
    }

}
