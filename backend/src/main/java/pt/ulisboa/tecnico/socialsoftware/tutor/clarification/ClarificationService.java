package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class ClarificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    QuestionClarificationRepository clarificationQuestionRepository;

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionAnswerRepository answerRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionClarificationDto createClarification(Integer questionId, Integer studentId, Integer answerId, QuestionClarificationDto questionClarificationDto) {
        if(questionClarificationDto.getContent() == null || questionClarificationDto.getContent().trim().isEmpty())
            throw new TutorException(USER_IS_NOT_A_TEACHER);

        if(studentId == null)
            throw new TutorException(USER_ID_IS_NULL);

        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, questionId));

        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));
        if(student.getRole() != User.Role.STUDENT)
            throw new TutorException(USER_IS_NOT_A_TEACHER);

        boolean valid = false;
        Set<QuizAnswer> quizAnswers = student.getQuizAnswers();
        if(!quizAnswers.isEmpty())
            for (QuizAnswer iQuizAnswer : quizAnswers) {
                List<QuestionAnswer> questionAnswers = iQuizAnswer.getQuestionAnswers();
                System.out.println(questionAnswers.size());
                if(!questionAnswers.isEmpty())
                    for (QuestionAnswer iAnswer: questionAnswers) {
                        if(iAnswer.getOption().getQuestion().getId() == questionId) {
                            valid = true;
                            break;
                        }
                    }
                if(valid) break;
            }

        if(!valid) throw new TutorException(USER_IS_NOT_A_TEACHER);


        QuestionAnswer answer = answerRepository.findById(answerId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, answerId));

        QuestionClarification questionClarification = new QuestionClarification(question, student, answer, questionClarificationDto);

        clarificationQuestionRepository.save(questionClarification);

        System.out.println(clarificationQuestionRepository.count());
        System.out.println(clarificationQuestionRepository.findAll().get(0).getId());

        /*checkClarificationId(clarificationQuestionId);

        checkTeacherId(teacherId);

        checkTeacherResponse(clarificationResponseDto);

        User teacher = getTeacher(teacherId);

        checkTeacherRole(teacher);

        QuestionClarification clarificationQuestion = getClarificationQuestion(clarificationQuestionId);

        ClarificationResponse clarificationResponse = createClarificationResponse(clarificationResponseDto, teacher, clarificationQuestion);*/

        return new QuestionClarificationDto(questionClarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationResponseDto answerClarification(Integer clarificationQuestionId, Integer teacherId, ClarificationResponseDto clarificationResponseDto) {
        checkClarificationId(clarificationQuestionId);

        checkTeacherId(teacherId);

        checkTeacherResponse(clarificationResponseDto);

        User teacher = getTeacher(teacherId);

        checkTeacherRole(teacher);

        QuestionClarification clarificationQuestion = getClarificationQuestion(clarificationQuestionId);

        ClarificationResponse clarificationResponse = createClarificationResponse(clarificationResponseDto, teacher, clarificationQuestion);

        return new ClarificationResponseDto(clarificationResponse);
    }

    private void checkClarificationId(Integer clarificationQuestionId) {
        if(clarificationQuestionId == null)
            throw new TutorException(CLARIFICATION_ID_IS_NULL);
    }

    private void checkTeacherId(Integer teacherId) {
        if(teacherId == null)
            throw new TutorException(USER_ID_IS_NULL);
    }

    private void checkTeacherResponse(ClarificationResponseDto clarificationResponseDto) {
        if(clarificationResponseDto.getTeacherResponse() == null || clarificationResponseDto.getTeacherResponse().trim().isEmpty())
            throw new TutorException(RESPONSE_CONTENT);
    }

    private User getTeacher(Integer teacherId) {
        return userRepository.findById(teacherId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));
    }

    private void checkTeacherRole(User teacher) {
        if(teacher.getRole() != User.Role.TEACHER)
            throw new TutorException(USER_IS_NOT_A_TEACHER);
    }

    private QuestionClarification getClarificationQuestion(Integer clarificationQuestionId) {
        return clarificationQuestionRepository.findById(clarificationQuestionId).orElseThrow(() -> new TutorException(QUESTION_CLARIFICATION_NOT_FOUND, clarificationQuestionId));
    }

    private ClarificationResponse createClarificationResponse(ClarificationResponseDto clarificationResponseDto, User teacher, QuestionClarification clarificationQuestion) {
        clarificationQuestion.setStatus(QuestionClarification.Status.ANSWERED);

        ClarificationResponse clarificationResponse = new ClarificationResponse(clarificationQuestion, teacher, clarificationResponseDto);
        clarificationResponseRepository.save(clarificationResponse);

        clarificationQuestion.addResponse(clarificationResponse);
        return clarificationResponse;
    }
}
