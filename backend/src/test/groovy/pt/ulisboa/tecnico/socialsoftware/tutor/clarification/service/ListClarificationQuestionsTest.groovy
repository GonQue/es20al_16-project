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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class ListClarificationQuestionsTest extends Specification {
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

        optionRepository.save(option)
        questionRepository.save(question)
        userRepository.save(student)
        answerRepository.save(answer)
        clarificationQuestionRepository.save(clarificationQuestion)
    }

    def 'list a NOT_ANSWERED clarificationQuestion'() {
        when:
        def result = clarificationService.listClarificationQuestions(student.getId())

        then: 'the returned data has a NOT_ASNWERED clarificationQuestion'
        result.size() == 1
        def resQuestion = result.get(0)
        resQuestion.getId() != null
        resQuestion.getContent() != CONTENT
        resQuestion.getStatus() == ClarificationQuestion.Status.NOT_ANSWERED.name()
        resQuestion.getCreationDate() != null
    }

    def 'list an ANSWERED clarificationQuestion'() {
        given: 'answer the question'
        def teacher = new User()
        teacher.setKey(2)
        teacher.setRole(User.Role.TEACHER)

        def clarificationResponse = new ClarificationResponse()
        clarificationResponse.setClarificationQuestion(clarificationQuestion)
        clarificationResponse.setTeacher(teacher)
        clarificationResponse.setTeacherResponse(TEACHER_RESPONSE)

        clarificationQuestion.addResponse(clarificationResponse)

        userRepository.save(teacher)
        clarificationResponseRepository.save(clarificationResponse)

        when:
        def result = clarificationService.listClarificationQuestions(student.getId())

        then: 'the returned data has an ASNWERED clarificationQuestion'
        result.size() == 1
        def resQuestion = result.get(0)
        resQuestion.getId() != null
        resQuestion.getContent() != CONTENT
        resQuestion.getStatus() == ClarificationQuestion.Status.ANSWERED.name()
        resQuestion.getCreationDate() != null
    }

    def 'list more than one clarificationQuestion'() {
        def secondQuestion = new Question()
        secondQuestion.setKey(2)
        secondQuestion.setContent(CONTENT)
        def secondAnswer = new QuestionAnswer()
        def secondQuizAnswer = new QuizAnswer()
        def secondOption = new Option()

        option.setQuestion(secondQuestion)
        answer.setOption(secondOption)
        quizAnswer.addQuestionAnswer(secondAnswer)
        student.addQuizAnswer(secondQuizAnswer)

        def secondClarificationQuestion = new ClarificationQuestion()
        secondClarificationQuestion.setQuestion(secondQuestion)
        secondClarificationQuestion.setStudent(student)
        secondClarificationQuestion.setAnswer(secondAnswer)
        secondClarificationQuestion.setContent(CONTENT)
        secondClarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)

        optionRepository.save(secondOption)
        questionRepository.save(secondQuestion)
        answerRepository.save(secondAnswer)

        when:
        def result = clarificationService.listClarificationQuestions(student.getId())

        then: 'the returned data has two clarificationQuestions'
        result.size() == 2
        def resFirstQuestion = result.get(0)
        resFirstQuestion.getId() != null
        resFirstQuestion.getContent() != CONTENT
        resFirstQuestion.getCreationDate() != null
        def resSecondQuestion = result.get(1)
        resSecondQuestion.getId() != null
        resSecondQuestion.getContent() != CONTENT
        resSecondQuestion.getCreationDate() != null
    }

    def 'user does not exist'() {
        when:
        clarificationService.listClarificationQuestions(UNEXISTENT_ID)

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_NOT_FOUND
    }

    def 'user id is null'() {
        when:
        clarificationService.listClarificationQuestions(null)

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_ID_IS_NULL
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        QuestionClarificationService() {
            return new ClarificationService()
        }
    }
}
