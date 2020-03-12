package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.QuestionClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll


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
    QuestionRepository questionRepository;

    @Autowired
    QuestionAnswerRepository answerRepository;

    @Autowired
    QuestionClarificationRepository questionClarificationRepository;

    @Autowired
    OptionRepository optionRepository;

    def questionClarification
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

        questionClarification = new QuestionClarification()
        questionClarification.setQuestion(question)
        questionClarification.setStudent(student)
        questionClarification.setAnswer(answer)
        questionClarification.setContent(CONTENT)

        optionRepository.save(option)
        questionRepository.save(question)
        userRepository.save(student)
        answerRepository.save(answer)
    }

    def 'create a clarification request'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(questionClarification.getQuestion().getId(), questionClarification.getStudent().getId(), questionClarification.getAnswer().getId(), questionClarificationDto)

        then: "the student is clarified"
        questionClarificationRepository.count() == 1L
        def result = questionClarificationRepository.findAll().get(0)
        result.getContent() == CONTENT
        result.getCreationDate() != null
    }

    def 'create a clarification request with non-existing question'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(UNEXISTENT_ID, questionClarification.getStudent().getId(), questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'create a clarification request for a question not answered by user'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())
        and: "new question, quizQuestion not answered"
        def newQuestion = new Question()
        newQuestion.setKey(2)
        newQuestion.setContent(CONTENT)

        questionRepository.save(newQuestion)

        when:
        clarificationService.createClarification(newQuestion.getId(), questionClarification.getStudent().getId(), questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the user does not exist'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(questionClarification.getQuestion().getId(), UNEXISTENT_ID, questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the user is not a student'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())
        and: "user is a teacher"
        def teacher = new User()
        teacher.setKey(1)
        teacher.setRole(User.Role.TEACHER)

        when:
        clarificationService.createClarification(questionClarification.getQuestion().getId(), teacher.getId(), questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the question does not exist'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(UNEXISTENT_ID, questionClarification.getStudent().getId(), questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the answer does not exist'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(questionClarification.getQuestion().getId(), questionClarification.getStudent().getId(), UNEXISTENT_ID, questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the clarification content is null'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(null)
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(questionClarification.getQuestion().getId(), questionClarification.getStudent().getId(), questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the clarification content is blank'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent("   ")
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(questionClarification.getQuestion().getId(), questionClarification.getStudent().getId(), questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the clarification userId is null'() {
        given: "create questionClarificationDto"
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(questionClarification.getId())
        questionClarificationDto.setContent(questionClarification.getContent())
        questionClarificationDto.setStatus(QuestionClarification.Status.NOT_ANSWERED.name())

        when:
        clarificationService.createClarification(questionClarification.getQuestion().getId(), null, questionClarification.getAnswer().getId(), questionClarificationDto)

        then:
        thrown(TutorException)
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        QuestionClarificationService() {
            return new ClarificationService()
        }
    }
}
