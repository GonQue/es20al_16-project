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
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

@DataJpaTest
class AdditionalClarificationTest {
    public static final String CONTENT = "clarificationQuestion content"
    public static final String TEACHER_RESPONSE = "teacher response"
    public static final Integer UNEXISTENT_ID = -1

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
        given: 'a question clarification that has already a response'
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

    def 'student asks for additional clarfications'() {
        when:
        clarificationService.askForAdditionalClarification(clarificationQuestion.getId())

        then: 'now the question needs a clarification again'
        clarificationQuestionRepository.count() == 1L
        def result = clarificationQuestionRepository.findAll().get(0)
        result.getStatus() == ClarificationQuestion.Status.ANSWERED
        result.getNeedClarification()
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
