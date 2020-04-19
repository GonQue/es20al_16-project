package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class ProposedQuestionController {

    @Autowired
    private ProposedQuestionService proposedQuestionService;

    @PutMapping("/proposed-questions/{proposedQuestionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#proposedQuestionId, 'PQ.ACCESS')")
    public ProposedQuestionDto teacherEvaluatesProposedQuestion(@PathVariable Integer proposedQuestionId, @Valid @RequestBody ProposedQuestionDto pqDto){
        return this.proposedQuestionService.teacherEvaluatesProposedQuestion(pqDto);
    }


    @PostMapping("/courses/{courseId}/proposed-questions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public ProposedQuestionDto createProposedQuestion(@PathVariable int courseId, @Valid @RequestBody ProposedQuestionDto propQuestionDto) {
        return proposedQuestionService.studentSubmitQuestion(courseId, propQuestionDto);
    }

    @GetMapping("/courses/{courseId}/student/proposed-questions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<ProposedQuestionDto> getStudentProposedQuestions(Principal principal, @PathVariable int courseId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }
        return proposedQuestionService.getStudentProposedQuestions(user.getId());
    }

    @GetMapping("/courses/{courseId}/proposed-questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<ProposedQuestionDto> getCourseProposedQuestions(Principal principal, @PathVariable int courseId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null){
            throw new TutorException(ErrorMessage.AUTHENTICATION_ERROR);
        }
        return proposedQuestionService.getCourseProposedQuestions(courseId);
    }

    @DeleteMapping("/proposed-questions/{proposedQuestionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#proposedQuestionId, 'PQ.CREATOR')")
    public ResponseEntity deleteProposedQuestion(@PathVariable Integer proposedQuestionId) {
        proposedQuestionService.deleteProposedQuestion(proposedQuestionId);
        return  ResponseEntity.ok().build();
    }
}