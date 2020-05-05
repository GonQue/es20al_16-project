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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class RemoveClarificationQuestionTest extends Specification {
    public static final String CONTENT = "CONTENT"
    public static final String TEACHER_RESPONSE = "TEACHER RESPONSE"

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
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository


    def clarificationQuestion
    def question
    def quizQuestion
    def student
    def answer
    def quizAnswer
    def option

    def setup() {
        student = new User()
        student.setKey(1)
        student.setRole(User.Role.STUDENT)

        question = new Question()
        question.setTitle("question title")
        question.setKey(1)
        question.setContent(CONTENT)

        quizQuestion = new QuizQuestion()

        answer = new QuestionAnswer()

        quizAnswer = new QuizAnswer()

        option = new Option()
        option.setContent("option content")
        option.setCorrect(true)
        option.setSequence(0)

        option.setQuestion(question)
        quizQuestion.setQuestion(question)
        answer.setOption(option)
        answer.setQuizQuestion(quizQuestion)
        quizAnswer.addQuestionAnswer(answer)
        student.addQuizAnswer(quizAnswer)

        clarificationQuestion = new ClarificationQuestion()
        clarificationQuestion.setQuestion(question)
        clarificationQuestion.setStudent(student)
        clarificationQuestion.setAnswer(answer)
        clarificationQuestion.setContent(CONTENT)

        quizQuestionRepository.save(quizQuestion)
        optionRepository.save(option)
        questionRepository.save(question)
        userRepository.save(student)
        answerRepository.save(answer)
        clarificationQuestionRepository.save(clarificationQuestion)
    }

    def "remove a clarification question without responses"() {
        when:
        clarificationService.removeClarification(clarificationQuestion.getId())

        then: "the question is removeQuestion"
        clarificationQuestionRepository.count() == 0L
        clarificationResponseRepository.count() == 0L
    }

    def "remove a clarification question with responses"() {
        given: "a teacher"
        def teacher = new User()
        teacher.setKey(2)
        teacher.setRole(User.Role.TEACHER)
        userRepository.save(teacher)
        and: "2 responses"
        def firstClarificationResponse = new ClarificationResponse()
        firstClarificationResponse.setTeacher(teacher)
        firstClarificationResponse.setTeacherResponse(TEACHER_RESPONSE)
        firstClarificationResponse.setClarificationQuestion(clarificationQuestion)
        clarificationResponseRepository.save(teacher)
        def secondClarificationResponse = new ClarificationResponse()
        secondClarificationResponse.setTeacher(teacher)
        secondClarificationResponse.setTeacherResponse(TEACHER_RESPONSE)
        secondClarificationResponse.setClarificationQuestion(clarificationQuestion)
        clarificationResponseRepository.save(teacher)

        when:
        clarificationService.removeClarification(clarificationQuestion.getId())

        then: "the question is removeQuestion"
        clarificationQuestionRepository.count() == 0L
        clarificationResponseRepository.count() == 0L
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
