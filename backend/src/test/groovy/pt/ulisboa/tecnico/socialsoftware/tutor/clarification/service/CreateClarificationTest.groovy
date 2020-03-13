package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_CONTENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWER_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWER_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class CreateClarificationTest extends Specification{
    public static final String CONTENT = "CONTENT"
    public static final Integer UNEXISTENT_ID = 99999999
    public static final Integer EXISTENT_ID = 1

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
    OptionRepository optionRepository

    def clarificationQuestion
    def question
    def student
    def answer
    def quizAnswer
    def option

    def setup() {
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

        optionRepository.save(option)
        questionRepository.save(question)
        userRepository.save(student)
        answerRepository.save(answer)
    }

    def 'create a clarification request'() {
        given: "create clarificationQuestionDto"
        def clarificationQuestionDto = new ClarificationQuestionDto()
        clarificationQuestionDto.setId(clarificationQuestion.getId())
        clarificationQuestionDto.setContent(clarificationQuestion.getContent())
        clarificationQuestionDto.setStatus(ClarificationQuestion.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(clarificationQuestion.getQuestion().getId(), clarificationQuestion.getStudent().getId(), clarificationQuestion.getAnswer().getId(), clarificationQuestionDto)

        then: "the student is clarified"
        clarificationQuestionRepository.count() == 1L
        def result = clarificationQuestionRepository.findAll().get(0)
        result.getContent() == CONTENT
        result.getCreationDate() != null
    }

    def 'create a clarification request with non-existing question'() {
        given: "create clarificationQuestionDto"
        def clarificationQuestionDto = new ClarificationQuestionDto()
        clarificationQuestionDto.setId(clarificationQuestion.getId())
        clarificationQuestionDto.setContent(clarificationQuestion.getContent())
        clarificationQuestionDto.setStatus(ClarificationQuestion.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(UNEXISTENT_ID, clarificationQuestion.getStudent().getId(), clarificationQuestion.getAnswer().getId(), clarificationQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'create a clarification request for a question not answered by user'() {
        given: "create clarificationQuestionDto"
        def clarificationQuestionDto = new ClarificationQuestionDto()
        clarificationQuestionDto.setId(clarificationQuestion.getId())
        clarificationQuestionDto.setContent(clarificationQuestion.getContent())
        clarificationQuestionDto.setStatus(ClarificationQuestion.Status.NOT_ANSWERED.name())
        and: "new question, quizQuestion not answered"
        def newQuestion = new Question()
        newQuestion.setKey(2)
        newQuestion.setContent(CONTENT)

        questionRepository.save(newQuestion)

        when:
        clarificationService.createClarification(newQuestion.getId(), clarificationQuestion.getStudent().getId(), clarificationQuestion.getAnswer().getId(), clarificationQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'the user is not a student'() {
        given: "create clarificationQuestionDto"
        def clarificationQuestionDto = new ClarificationQuestionDto()
        clarificationQuestionDto.setId(clarificationQuestion.getId())
        clarificationQuestionDto.setContent(clarificationQuestion.getContent())
        clarificationQuestionDto.setStatus(ClarificationQuestion.Status.NOT_ANSWERED.name())
        and: "user is a teacher"
        def teacher = new User()
        teacher.setKey(1)
        teacher.setRole(User.Role.TEACHER)

        when:
        clarificationService.createClarification(clarificationQuestion.getQuestion().getId(), teacher.getId(), clarificationQuestion.getAnswer().getId(), clarificationQuestionDto)

        then:
        thrown(TutorException)
    }

    @Unroll("nonexistent objects: #userId | #questionId | #answerId || errorMessage")
    def "user/question/answer does not exist"(){
        given: "a clarificationQuestionDto"
        def clarificationQuestionDto = new ClarificationQuestionDto()
        clarificationQuestionDto.setId(clarificationQuestion.getId())
        clarificationQuestionDto.setContent(clarificationQuestion.getContent())
        clarificationQuestionDto.setStatus(ClarificationQuestion.Status.NOT_ANSWERED.name())
        and: "ids"
        def sId = studentId = studentId == UNEXISTENT_ID ? UNEXISTENT_ID : student.getId()
        def qId = questionId = questionId == UNEXISTENT_ID ? UNEXISTENT_ID : question.getId()
        def aId = answerId = answerId == UNEXISTENT_ID ? UNEXISTENT_ID : answer.getId()

        when:
        clarificationService.createClarification(qId, sId, aId, clarificationQuestionDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        studentId          | questionId     | answerId      || errorMessage
        UNEXISTENT_ID      | EXISTENT_ID    | EXISTENT_ID   || USER_NOT_FOUND
        EXISTENT_ID        | UNEXISTENT_ID  | EXISTENT_ID   || QUESTION_NOT_FOUND
        EXISTENT_ID        | EXISTENT_ID    | UNEXISTENT_ID || QUESTION_ANSWER_NOT_FOUND
    }

    @Unroll("invalid arguments: #studentId | #questionId | #answerId | #content || errorMessage")
    def "invalid input values"() {
        given: "create clarificationQuestionDto"
        def clarificationQuestionDto = new ClarificationQuestionDto()
        clarificationQuestionDto.setId(clarificationQuestion.getId())
        and: "input to test"
        def sId = studentId = studentId == null ? null : student.getId()
        def qId = questionId = questionId == null ? null : question.getId()
        def aId = answerId = answerId == null ? null : answer.getId()
        clarificationQuestionDto.setContent(content)

        when:
        clarificationService.createClarification(qId,sId,aId, clarificationQuestionDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        studentId          | questionId     | answerId      | content   || errorMessage
        null               | EXISTENT_ID    | EXISTENT_ID   | CONTENT   || USER_ID_IS_NULL
        EXISTENT_ID        | null           | EXISTENT_ID   | CONTENT   || QUESTION_ID_IS_NULL
        EXISTENT_ID        | EXISTENT_ID    | null          | CONTENT   || QUESTION_ANSWER_ID_IS_NULL
        EXISTENT_ID        | EXISTENT_ID    | EXISTENT_ID   | null      || CLARIFICATION_CONTENT
        EXISTENT_ID        | EXISTENT_ID    | EXISTENT_ID   | "   "     || CLARIFICATION_CONTENT
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        QuestionClarificationService() {
            return new ClarificationService()
        }
    }
}
