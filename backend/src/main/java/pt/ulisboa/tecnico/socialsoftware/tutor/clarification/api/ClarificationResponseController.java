package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;

@RestController
public class ClarificationResponseController{

    @Autowired
    private ClarificationService clarificationService;

    @PostMapping("/teacher/clarifications/{clarificationId}/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ClarificationResponseDto createClarificationResponse(Principal principal, @PathVariable Integer clarificationId, @RequestBody ClarificationResponseDto clarificationResponseDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return clarificationService.answerClarification(clarificationId, user.getId(), clarificationResponseDto);
    }
}