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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreateClarificationPerformanceTest extends Specification{
    public static final String CONTENT = "CONTENT"

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

    def 'performance testing to create a clarification request'() {
        given: "create clarificationQuestionDto"
        def clarificationQuestionDto = new ClarificationQuestionDto()
        clarificationQuestionDto.setId(clarificationQuestion.getId())
        clarificationQuestionDto.setAnswerId(clarificationQuestion.getAnswer().getId())
        clarificationQuestionDto.setContent(clarificationQuestion.getContent())
        clarificationQuestionDto.setStatus(ClarificationQuestion.Status.NOT_ANSWERED.name())

        when:
        1.upto(1, {
            clarificationService.createClarification(clarificationQuestion.getQuestion().getId(), clarificationQuestion.getStudent().getId(), clarificationQuestionDto)
        })

        then:
        true
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
