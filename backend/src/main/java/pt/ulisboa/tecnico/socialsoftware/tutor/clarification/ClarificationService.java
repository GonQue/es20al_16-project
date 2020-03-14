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
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class ClarificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository;

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
    public ClarificationQuestionDto createClarification(Integer questionId, Integer studentId, Integer answerId, ClarificationQuestionDto clarificationQuestionDto) {

        checkClarificationContent(clarificationQuestionDto);

        checkStudentId(studentId);

        checkQuestionId(questionId);

        checkAnswerId(answerId);

        Question question = getQuestion(questionId);

        User student = getStudent(studentId);

        checkQuestionAnswers(questionId, student);

        QuestionAnswer answer = getAnswer(answerId);

        ClarificationQuestion clarificationQuestion = createClarificationQuestion(clarificationQuestionDto, question, student, answer);

        return new ClarificationQuestionDto(clarificationQuestion);
    }

    private void checkClarificationContent(ClarificationQuestionDto clarificationQuestionDto) {
        if(clarificationQuestionDto.getContent() == null || clarificationQuestionDto.getContent().trim().isEmpty())
            throw new TutorException(CLARIFICATION_CONTENT);
    }

    private void checkStudentId(Integer studentId) {
        if(studentId == null)
            throw new TutorException(USER_ID_IS_NULL);
    }

    private void checkQuestionId(Integer questionId) {
        if(questionId == null)
            throw new TutorException(QUESTION_ID_IS_NULL);
    }

    private void checkAnswerId(Integer answerId) {
        if(answerId == null)
            throw new TutorException(QUESTION_ANSWER_ID_IS_NULL);
    }

    private Question getQuestion(Integer questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    private User getStudent(Integer studentId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));
        if(student.getRole() != User.Role.STUDENT)
            throw new TutorException(USER_IS_NOT_A_STUDENT);
        return student;
    }

    private void checkQuestionAnswers(Integer questionId, User student) {
        boolean valid = false;
        Set<QuizAnswer> quizAnswers = student.getQuizAnswers();
        if(!quizAnswers.isEmpty())
            for (QuizAnswer iQuizAnswer : quizAnswers) {
                List<QuestionAnswer> questionAnswers = iQuizAnswer.getQuestionAnswers();
                if(!questionAnswers.isEmpty())
                    for (QuestionAnswer iAnswer: questionAnswers) {
                        if(iAnswer.getOption().getQuestion().getId() == questionId) {
                            valid = true;
                            break;
                        }
                    }
                if(valid) break;
            }

        if(!valid) throw new TutorException(QUESTION_ANSWERS_NOT_FOUND);
    }

    private QuestionAnswer getAnswer(Integer answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answerId));
    }

    private ClarificationQuestion createClarificationQuestion(ClarificationQuestionDto clarificationQuestionDto, Question question, User student, QuestionAnswer answer) {
        ClarificationQuestion clarificationQuestion = new ClarificationQuestion(question, student, answer, clarificationQuestionDto);

        clarificationQuestionRepository.save(clarificationQuestion);

        question.addClarificationQuestion(clarificationQuestion);
        student.addClarificationQuestion(clarificationQuestion);
        answer.addClarificationQuestion(clarificationQuestion);

        return clarificationQuestion;
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

        ClarificationQuestion clarificationQuestion = getClarificationQuestion(clarificationQuestionId);

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

    private ClarificationQuestion getClarificationQuestion(Integer clarificationQuestionId) {
        return clarificationQuestionRepository.findById(clarificationQuestionId).orElseThrow(() -> new TutorException(QUESTION_CLARIFICATION_NOT_FOUND, clarificationQuestionId));
    }

    private ClarificationResponse createClarificationResponse(ClarificationResponseDto clarificationResponseDto, User teacher, ClarificationQuestion clarificationQuestion) {
        clarificationQuestion.setStatus(ClarificationQuestion.Status.ANSWERED);

        ClarificationResponse clarificationResponse = new ClarificationResponse(clarificationQuestion, teacher, clarificationResponseDto);
        clarificationResponseRepository.save(clarificationResponse);

        clarificationQuestion.addResponse(clarificationResponse);
        teacher.addClarificationResponse(clarificationResponse);

        return clarificationResponse;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationResponseDto> listResponses(Integer clarificationQuestionId) {

        if(clarificationQuestionId == null)
            throw new TutorException(CLARIFICATION_ID_IS_NULL);
        ClarificationQuestion clarificationQuestion = clarificationQuestionRepository.findById(clarificationQuestionId).orElseThrow(() -> new TutorException(QUESTION_CLARIFICATION_NOT_FOUND, clarificationQuestionId));

        return clarificationQuestion.getResponses().stream().map(ClarificationResponseDto::new).collect(Collectors.toList());
    }
}