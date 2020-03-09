package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationResponseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.QuestionClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.QuestionClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.QuestionClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.QuestionClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class AnswerClarificationTest extends Specification{
    public static final String TEACHER_RESPONSE = "teacher response"

    @Autowired
    QuestionClarificationService questionClarificationService

    @Autowired
    QuestionClarificationRepository questionClarificationRepository

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository;

    @Autowired
    UserRepository userRepository

    def questionClarification
    def teacher
    def clarificationResponse

    def setup() {
        given: "create a teacher to answer the clarification"
        teacher = new User('name', "username", 1, User.Role.TEACHER)
        and: "create a question clarification need"
        questionClarification = new QuestionClarification()
        questionClarification.setStatus(QuestionClarification.Status.NOT_ANSWERED)
        and: "create a response"
        clarificationResponse = new ClarificationResponse()
        clarificationResponse.setTeacherResponse(TEACHER_RESPONSE)

        userRepository.save(teacher)
        questionClarificationRepository.save(questionClarification)
    }

    def 'the teacher clarifies a student'() {
        given: "create clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())

        when:
        questionClarificationService.answerClarification(questionClarification.getId(), teacher.getId(), clarificationResponseDto)

        then: "the need is clarified"
        clarificationResponseRepository.count() == 1L
        def result = clarificationResponseRepository.findAll().get(0)
        result.getTeacherResponse() == TEACHER_RESPONSE
        result.getResponseDate() != null
        and: "the status of the question clarification was set to answered"
        questionClarificationRepository.count() == 1L
        def questionClarificationResult = questionClarificationRepository.findAll().get(0)
        questionClarificationResult.getStatus().name() == QuestionClarification.Status.ANSWERED.name()
        questionClarificationResult.getResponses().size() == 1L
    }

    def 'two teachers clarify the same student'() {
        given: "create clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())
        and: "a second teacher"
        def second_teacher = new User('name2', "username2", 2, User.Role.TEACHER)
        userRepository.save(second_teacher)

        when:
        questionClarificationService.answerClarification(questionClarification.getId(), teacher.getId(), clarificationResponseDto)
        questionClarificationService.answerClarification(questionClarification.getId(), second_teacher.getId(), clarificationResponseDto)

        then: "the need is clarified"
        clarificationResponseRepository.count() == 2L
        def firstTeacherResult = clarificationResponseRepository.findAll().get(0)
        firstTeacherResult.getTeacherResponse() == TEACHER_RESPONSE
        firstTeacherResult.getResponseDate() != null
        def secondTeacherResult = clarificationResponseRepository.findAll().get(1)
        secondTeacherResult.getTeacherResponse() == TEACHER_RESPONSE
        secondTeacherResult.getResponseDate() != null
        and: "the status of the question clarification was set to answered"
        questionClarificationRepository.count() == 1L
        def questionClarificationResult = questionClarificationRepository.findAll().get(0)
        questionClarificationResult.getStatus().name() == QuestionClarification.Status.ANSWERED.name()
        questionClarificationResult.getResponses().size() == 2L
    }

    def 'the user is not a teacher'() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())
        and: "student"
        def student = new User('student', "student", 2, User.Role.STUDENT)
        userRepository.save(student)

        when:
        questionClarificationService.answerClarification(questionClarification.getId(), student.getId(), clarificationResponseDto)

        then:
        thrown(TutorException)
    }

    def 'the teacher with the id does not exist'() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())

        when:
        questionClarificationService.answerClarification(questionClarification.getId(), 99, clarificationResponseDto)

        then:
        thrown(TutorException)
    }

    def 'the question clarification with the id does not exist'() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())

        when:
        questionClarificationService.answerClarification(99, teacher.getId(), clarificationResponseDto)

        then:
        thrown(TutorException)
    }

    def 'teacher id is null'() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())

        when:
        questionClarificationService.answerClarification(questionClarification.getId(), null, clarificationResponseDto)

        then:
        thrown(TutorException)
    }

    def 'teacher response is null'() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        and: "response null"
        clarificationResponseDto.setTeacherResponse(null)

        when:
        questionClarificationService.answerClarification(questionClarification.getId(), teacher.getId(), clarificationResponseDto)

        then:
        thrown(TutorException)
    }

    def 'teacher response is blank'() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        and: "response blank"
        clarificationResponseDto.setTeacherResponse("   ")

        when:
        questionClarificationService.answerClarification(questionClarification.getId(), teacher.getId(), clarificationResponseDto)

        then:
        thrown(TutorException)
    }

    @TestConfiguration
    static class QuestionClarificationServiceImplTestContextConfiguration {

        @Bean
        QuestionClarificationService() {
            return new QuestionClarificationService()
        }
    }
}
