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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetClarificationQuestionsPerformanceTest extends Specification {
    public static final String CONTENT = "clarificationQuestion content"

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

    def setup() {
    }

    def 'performance testing to get 1000 clarification questions'() {
        given: '1000 clarification questions'
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

        1.upto(1, {
            def clarificationQuestion = new ClarificationQuestion()
            clarificationQuestion.setQuestion(question)
            clarificationQuestion.setStudent(student)
            clarificationQuestion.setAnswer(answer)
            clarificationQuestion.setContent(CONTENT)
            clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
            student.addClarificationQuestion(clarificationQuestion)
            clarificationQuestionRepository.save(clarificationQuestion)
        })

        optionRepository.save(option)
        questionRepository.save(question)
        userRepository.save(student)
        answerRepository.save(answer)

        when:
        1.upto(1, {
            clarificationService.listClarificationQuestions(student.getId())
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
