package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationResponseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWERS_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class ListPublicClarificationQuestionsTest extends Specification {
    public static final String CONTENT = "clarificationQuestion content"
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
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    OptionRepository optionRepository

    def firstStudent
    def secondStudent
    def question
    def answer
    def secondAnswer
    def quizAnswer
    def secondQuizAnswer
    def option
    def secondOption
    def clarificationQuestion
    def quizQuestion

    def setup() {
        given: 'a clarification question'
        firstStudent = new User()
        firstStudent.setKey(1)
        firstStudent.setRole(User.Role.STUDENT)

        question = new Question()
        question.setKey(1)
        question.setContent(CONTENT)

        quizQuestion = new QuizQuestion()

        answer = new QuestionAnswer()

        quizAnswer = new QuizAnswer()

        option = new Option()

        option.setQuestion(question)
        quizQuestion.setQuestion(question)
        answer.setOption(option)
        answer.setQuizQuestion(quizQuestion)
        quizAnswer.addQuestionAnswer(answer)
        firstStudent.addQuizAnswer(quizAnswer)

        clarificationQuestion = new ClarificationQuestion()
        clarificationQuestion.setQuestion(question)
        clarificationQuestion.setStudent(firstStudent)
        clarificationQuestion.setAnswer(answer)
        clarificationQuestion.setContent(CONTENT)
        clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
        clarificationQuestion.setAvailableToOtherStudents(true);

        firstStudent.addClarificationQuestion(clarificationQuestion)

        quizQuestionRepository.save(quizQuestion)
        optionRepository.save(option)
        questionRepository.save(question)
        userRepository.save(firstStudent)
        answerRepository.save(answer)
        clarificationQuestionRepository.save(clarificationQuestion)

        and: 'a student with no clarificationQuestions'
        secondStudent = new User()
        secondStudent.setKey(2)
        secondStudent.setRole(User.Role.STUDENT)

        secondAnswer = new QuestionAnswer()

        secondQuizAnswer = new QuizAnswer()

        secondOption = new Option()

        secondAnswer.setOption(secondOption)
        secondAnswer.setQuizQuestion(quizQuestion)
        secondAnswer.setQuizAnswer(secondQuizAnswer)
        secondQuizAnswer.addQuestionAnswer(secondAnswer)
        secondQuizAnswer.setUser(secondStudent)
        secondStudent.addQuizAnswer(secondQuizAnswer)

        optionRepository.save(secondOption)
        userRepository.save(secondStudent)
        answerRepository.save(secondAnswer)
        quizAnswerRepository.save(secondQuizAnswer)
    }

    def 'list a public clarificationQuestion about a question answered by second student'() {
        when:
        def result = clarificationService.listPublicClarificationQuestions(secondStudent.getId(), question.getId())

        then: 'the returned data has a clarificationQuestion from another student'
        result.size() == 1
        def resQuestion = result.get(0)
        resQuestion.getId() != null
        resQuestion.getContent() == CONTENT
        resQuestion.getStatus() == ClarificationQuestion.Status.NOT_ANSWERED.name()
    }

    def 'list more than one public clarificationQuestion about a question answered by second student'() {
        given: 'a second public clarification'
        def secondClarificationQuestion = new ClarificationQuestion()
        secondClarificationQuestion.setQuestion(question)
        secondClarificationQuestion.setStudent(firstStudent)
        secondClarificationQuestion.setAnswer(answer)
        secondClarificationQuestion.setContent(CONTENT)
        secondClarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
        secondClarificationQuestion.setAvailableToOtherStudents(true);

        firstStudent.addClarificationQuestion(secondClarificationQuestion)

        clarificationQuestionRepository.save(secondClarificationQuestion)

        when:
        def result = clarificationService.listPublicClarificationQuestions(secondStudent.getId(), question.getId())

        then: 'the returned data has two clarificationQuestion from other students'
        result.size() == 2
        def resFirstClarificationQuestion = result.get(0)
        resFirstClarificationQuestion.getId() != null
        resFirstClarificationQuestion.getContent() == CONTENT
        def resSecondClarificationQuestion = result.get(1)
        resSecondClarificationQuestion.getId() != null
        resSecondClarificationQuestion.getContent() == CONTENT
    }

    def 'filter private clarifications'() {
        given: 'a private clarification'
        def privateClarificationQuestion = new ClarificationQuestion()
        privateClarificationQuestion.setQuestion(question)
        privateClarificationQuestion.setStudent(firstStudent)
        privateClarificationQuestion.setAnswer(answer)
        privateClarificationQuestion.setContent(CONTENT)
        privateClarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
        privateClarificationQuestion.setAvailableToOtherStudents(false);

        firstStudent.addClarificationQuestion(privateClarificationQuestion)

        clarificationQuestionRepository.save(privateClarificationQuestion)

        when:
        def result = clarificationService.listPublicClarificationQuestions(secondStudent.getId(), question.getId())

        then: 'the returned data has only one clarificationQuestion from another student; the new one is rejected'
        result.size() == 1
    }

    def 'user has not answered the question'() {
        given: 'a student that has not answered the question'
        def thirdStudent = new User()
        thirdStudent.setKey(3)
        thirdStudent.setRole(User.Role.STUDENT)

        userRepository.save(thirdStudent)

        when:
        clarificationService.listPublicClarificationQuestions(thirdStudent.getId(), question.getId())

        then: 'the returned data has a clarificationQuestion from another student'
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWERS_NOT_FOUND
    }

    def 'user does not exist'() {
        when:
        clarificationService.listPublicClarificationQuestions(UNEXISTENT_ID, question.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_NOT_FOUND
    }

    def 'user id is null'() {
        when:
        clarificationService.listPublicClarificationQuestions(null, question.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_ID_IS_NULL
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
