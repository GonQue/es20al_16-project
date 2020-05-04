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

    @PostMapping("/student/clarifications/{questionId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ClarificationQuestionDto createClarificationQuestion(Principal principal, @PathVariable Integer questionId, @RequestBody ClarificationQuestionDto clarificationQuestionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.createClarification(questionId, user.getId(), clarificationQuestionDto);
    }

    @DeleteMapping("/student/clarifications/{clarificationQuestionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void removeClarificationQuestion(Principal principal, @PathVariable Integer clarificationQuestionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        clarificationService.removeClarification(clarificationQuestionId);
    }

    @GetMapping("/student/clarifications/status")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationQuestionDto> getClarificationQuestions(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.listClarificationQuestions(user.getId());
    }

    @GetMapping("/student/clarifications/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationQuestionDto> getOtherPublicClarificationQuestions(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.listOtherPublicClarificationQuestions(user.getId());
    }

    @GetMapping("/student/clarifications/{clarificationId}/responses")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationResponseDto> listStudentResponses(Principal principal, @PathVariable Integer clarificationId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.listResponses(clarificationId);
    }

    @PostMapping("/management/clarifications/{clarificationId}/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ClarificationResponseDto createClarificationResponse(Principal principal, @PathVariable Integer clarificationId, @RequestBody ClarificationResponseDto clarificationResponseDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.answerClarification(clarificationId, user.getId(), clarificationResponseDto);
    }

    @GetMapping("/management/clarifications/{clarificationId}/responses")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<ClarificationResponseDto> listTeacherResponses(Principal principal, @PathVariable Integer clarificationId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.listResponses(clarificationId);
    }

    @GetMapping("/management/clarifications/status")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<ClarificationQuestionDto> getAllClarificationQuestions(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return clarificationService.listAllClarificationQuestions();
    }

    @DeleteMapping("/management/clarifications/{clarificationResponseId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public void removeClarificationResponse(Principal principal, @PathVariable Integer clarificationResponseId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        clarificationService.removeClarificationResponse(clarificationResponseId);
    }

    @PostMapping("/student/clarifications/{clarificationQuestionId}/additional")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void askForAdditionalClarification(Principal principal, @PathVariable Integer clarificationQuestionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        clarificationService.askForAdditionalClarification(clarificationQuestionId);
    }

    @PostMapping("/management/clarifications/{clarificationId}/availability")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public void changeClarificationAvailability(Principal principal, @PathVariable Integer clarificationId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        clarificationService.changeClarificationAvailability(clarificationId);
    }
}
