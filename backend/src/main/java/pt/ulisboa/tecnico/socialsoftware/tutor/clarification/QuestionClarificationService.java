package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QuestionClarificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    QuestionClarificationRepository questionClarificationRepository;

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationResponseDto answerClarification(Integer questionClarificationId, Integer teacherId, ClarificationResponseDto clarificationResponseDto) {
        if(teacherId == null) throw new TutorException(USER_ID_IS_NULL);

        if(clarificationResponseDto.getTeacherResponse() == null || clarificationResponseDto.getTeacherResponse().trim().isEmpty())
            throw new TutorException(RESPONSE_CONTENT);

        User user = userRepository.findById(teacherId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if(user.getRole() != User.Role.TEACHER) throw new TutorException(USER_IS_NOT_A_TEACHER);

        QuestionClarification questionClarification = questionClarificationRepository.findById(questionClarificationId).orElseThrow(() -> new TutorException(QUESTION_CLARIFICATION_NOT_FOUND, questionClarificationId));
        questionClarification.setStatus(QuestionClarification.Status.ANSWERED);

        ClarificationResponse clarificationResponse = new ClarificationResponse(questionClarification, user, clarificationResponseDto);
        clarificationResponseRepository.save(clarificationResponse);

        questionClarification.addResponse(clarificationResponse);

        return new ClarificationResponseDto(clarificationResponse);
    }
}
