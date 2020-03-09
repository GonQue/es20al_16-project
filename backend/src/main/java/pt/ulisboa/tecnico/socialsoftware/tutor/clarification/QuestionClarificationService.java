package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class QuestionClarificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    QuestionClarificationRepository questionClarificationRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionClarificationDto answerClarification(QuestionClarificationDto questionClarificationDto) {
        if(questionClarificationDto.getTeacherId() == null) throw new TutorException(USER_NOT_FOUND, -1);
        User user = userRepository.findById(questionClarificationDto.getTeacherId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, questionClarificationDto.getTeacherId()));
        if(user.getRole() != User.Role.TEACHER) throw new TutorException(USER_NOT_FOUND, questionClarificationDto.getTeacherId());

        QuestionClarification questionClarification = questionClarificationRepository.findById(questionClarificationDto.getId()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionClarificationDto.getId()));
        questionClarification.answerQuestionClarification(questionClarificationDto);
        return new QuestionClarificationDto(questionClarification);
    }
}
