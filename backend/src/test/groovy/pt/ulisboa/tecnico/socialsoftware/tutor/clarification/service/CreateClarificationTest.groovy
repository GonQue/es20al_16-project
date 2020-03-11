package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.QuestionClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateClarificationTest extends Specification{
    public static final String CONTENT_ONE = "Conteudo do pedido"
    public static final String QUESTION_TITLE = "question title"
    public static final String QUESTION_CONTENT = "question content"
    public static final String OPTION_CONTENT = "optionId content"

    def questionClarificationService
    def questionDto
    def optionDto
    def options
    def questionAnswer
    def quizAnswer
    def user
    def setup() {
         questionClarificationService = new QuestionClarificationService()

        //create a questionDto
        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        //create an optionDto
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        //create a questionAnswer
        questionAnswer = new QuestionAnswer()
        questionAnswer.setId(1)
        optionDto.addQuestionAnswer(questionAnswer)

        //create a QuizAnswer
        quizAnswer = new QuizAnswer()
        quizAnswer.setId(1)
        questionAnswer.setQuizAnswer(quizAnswer)

        //create a Student
        user = new User()
        user.setId(1)
        user.setRole(User.Role.STUDENT)
        quizAnswer.setUser(user)
    }

    def 'create a clarification request'(){
        given: "a QuestionClarificationDto"
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        def creationDate = LocalDateTime.now()
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(1)
        questionClarificationDto.setUserId(1)
        questionClarificationDto.setQuestionId(1)
        questionClarificationDto.setContent(CONTENT_ONE)
        questionClarificationDto.setStatus("NOT_ANSWERED")
        questionClarificationDto.setCreationDate(creationDate.format(formatter))
        when:
        def result = questionClarificationService.studentCreateClarificationRequest(questionClarificationDto)
        then:
        result.getId() == 1
        result.getUserId() == 1
        result.getContent() == CONTENT_ONE
        result.getQuestionId() == 1
        result.getStatus() == "NOT_ANSWERED"
        result.getCreationDate() == creationDate.format(formatter)
    }

    def 'create a clarification request with non-existing question'(){
        given: "a QuestionClarificationDto"
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        def creationDate = LocalDateTime.now()
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(1)
        questionClarificationDto.setUserId(1)
        questionClarificationDto.setContent(CONTENT_ONE)
        questionClarificationDto.setStatus("NOT_ANSWERED")
        questionClarificationDto.setCreationDate(creationDate.format(formatter))
        and: "no question"
        questionClarificationDto.setQuestionId(null)

        when:
        questionClarificationService.studentCreateClarificationRequest(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'create a clarification request for a question not answered by user'(){
        given: "a QuestionClarificationDto"
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        def creationDate = LocalDateTime.now()
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setId(1)
        questionClarificationDto.setUserId(1)
        questionClarificationDto.setContent(CONTENT_ONE)
        questionClarificationDto.setStatus("NOT_ANSWERED")
        questionClarificationDto.setCreationDate(creationDate.format(formatter))

        and: "a QuestionDto and answer for a different user"
        questionDto = new QuestionDto()
        questionDto.setKey(2)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        questionAnswer = new QuestionAnswer()
        questionAnswer.setId(2)
        optionDto.addQuestionAnswer(questionAnswer)
        quizAnswer = new QuizAnswer()
        quizAnswer.setId(2)
        questionAnswer.setQuizAnswer(quizAnswer)
        user = new User()
        user.setId(2)
        user.setRole(User.Role.STUDENT)
        quizAnswer.setUser(user)
        questionClarificationDto.setQuestionId(2)

        when:
        questionClarificationService.studentCreateClarificationRequest(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the user is not a student'(){
        given: 'a teacher'
        def user = new User()
        user.setId(2)
        user.setRole(User.Role.TEACHER)
        and: 'a QuestionClarificationDto'
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setUserId(1)

        when:
        questionClarificationService.studentCreateClarificationRequest(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the clarification content is null'(){
        given: 'a QuestionClarificationDto'
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setUserId(1)
        questionClarificationDto.setContent(null)

        when:
        questionClarificationService.studentCreateClarificationRequest(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the clarification content is blank'(){
        given: 'a QuestionClarificationDto'
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setUserId(1)
        questionClarificationDto.setContent(' ')

        when:
        questionClarificationService.studentCreateClarificationRequest(questionClarificationDto)

        then:
        thrown(TutorException)
    }

    def 'the clarification userId is blank'(){
        given: 'a QuestionClarificationDto'
        def questionClarificationDto = new QuestionClarificationDto()
        questionClarificationDto.setUserId(0)
        questionClarificationDto.setContent(CONTENT_ONE)

        when:
        questionClarificationService.studentCreateClarificationRequest(questionClarificationDto)

        then:
        thrown(TutorException)
    }
}
