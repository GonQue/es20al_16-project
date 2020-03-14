package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationResponseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_CLARIFICATION_NOT_FOUND

@DataJpaTest
class ListResponsesTest extends Specification {
    public static final String CONTENT = "clarificationQuestion content"
    public static final String TEACHER_RESPONSE = "teacher response"
    public static final Integer UNEXISTENT_ID = 99999999

    @Autowired
    ClarificationService clarificationService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionAnswerRepository answerRepository

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository

    @Autowired
    OptionRepository optionRepository

    def student
    def question
    def answer
    def quizAnswer
    def option
    def clarificationQuestion
    def teacher
    def clarificationResponse

    def setup() {
        given: 'one response'
        student = new User()
        student.setKey(1)
        student.setRole(User.Role.STUDENT)
        question = new Question()
        question.setKey(1)
        question.setContent(CONTENT)
        answer = new QuestionAnswer()
        quizAnswer = new QuizAnswer()
        option = new Option()

        option.setQuestion(question)
        answer.setOption(option)
        quizAnswer.addQuestionAnswer(answer)
        student.addQuizAnswer(quizAnswer)

        clarificationQuestion = new ClarificationQuestion()
        clarificationQuestion.setQuestion(question)
        clarificationQuestion.setStudent(student)
        clarificationQuestion.setAnswer(answer)
        clarificationQuestion.setContent(CONTENT)
        clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)

        teacher = new User()
        teacher.setKey(2)
        teacher.setRole(User.Role.TEACHER)

        clarificationResponse = new ClarificationResponse()
        clarificationResponse.setClarificationQuestion(clarificationQuestion)
        clarificationResponse.setTeacher(teacher)
        clarificationResponse.setTeacherResponse(TEACHER_RESPONSE)

        clarificationQuestion.addResponse(clarificationResponse)

        optionRepository.save(option)
        questionRepository.save(question)
        userRepository.save(student)
        answerRepository.save(answer)
        clarificationQuestionRepository.save(clarificationQuestion)
        userRepository.save(teacher)
        clarificationResponseRepository.save(clarificationResponse)
    }

    def 'associate one response with a clarficationQuestion and list it'() {
        when:
        def result = clarificationService.listResponses(clarificationQuestion.getId())

        then: 'the returned data has one response'
        result.size() == 1
        def resResponse = result.get(0)
        resResponse.getId() != null
        resResponse.getTeacherResponse() == TEACHER_RESPONSE
    }

    def 'associate two responses to a clarficationQuestion and list them'() {
        given: 'a second response'
        def secondTeacher = new User()
        secondTeacher.setKey(3)
        secondTeacher.setRole(User.Role.TEACHER)

        def secondClarificationResponse = new ClarificationResponse()
        secondClarificationResponse.setClarificationQuestion(clarificationQuestion)
        secondClarificationResponse.setTeacher(secondTeacher)
        secondClarificationResponse.setTeacherResponse(TEACHER_RESPONSE)

        clarificationQuestion.addResponse(secondClarificationResponse)

        userRepository.save(secondTeacher)
        clarificationResponseRepository.save(secondClarificationResponse)

        when:
        def result = clarificationService.listResponses(clarificationQuestion.getId())

        then: 'the returned data have two responses'
        result.size() == 2
        def resFirstResponse = result.get(0)
        resFirstResponse.getId() != null
        resFirstResponse.getTeacherResponse() == TEACHER_RESPONSE
        def resSecondResponse = result.get(1)
        resSecondResponse.getId() != null
        resSecondResponse.getTeacherResponse() == TEACHER_RESPONSE
    }

    def 'question clarification does not exist'() {
        when:
        clarificationService.listResponses(UNEXISTENT_ID)

        then:
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_CLARIFICATION_NOT_FOUND
    }

    def 'question clarification id is null'() {
        when:
        clarificationService.listResponses(null)

        then:
        def error = thrown(TutorException)
        error.errorMessage == CLARIFICATION_ID_IS_NULL
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        QuestionClarificationService() {
            return new ClarificationService()
        }
    }
}
