package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationResponseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_CLARIFICATION_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.RESPONSE_CONTENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_ID_IS_NULL
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_NOT_A_TEACHER
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class AnswerClarificationTest extends Specification{
    public static final String TEACHER_RESPONSE = "teacher response"
    public static final Integer UNEXISTENT_ID = -1
    public static final Integer EXISTENT_ID = 1

    @Autowired
    ClarificationService clarificationService

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository

    @Autowired
    UserRepository userRepository

    def clarificationQuestion
    def teacher
    def clarificationResponse

    def setup() {
        teacher = new User()
        teacher.setKey(1)
        teacher.setRole(User.Role.TEACHER)

        clarificationQuestion = new ClarificationQuestion()
        clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)

        clarificationResponse = new ClarificationResponse()
        clarificationResponse.setTeacherResponse(TEACHER_RESPONSE)

        userRepository.save(teacher)
        clarificationQuestionRepository.save(clarificationQuestion)
    }

    def 'the teacher clarifies a student'() {
        given: "create clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())

        when:
        clarificationService.answerClarification(clarificationQuestion.getId(), teacher.getId(), clarificationResponseDto)

        then: "the student is clarified"
        clarificationResponseRepository.count() == 1L
        def result = clarificationResponseRepository.findAll().get(0)
        result.getTeacherResponse() == TEACHER_RESPONSE
        result.getResponseDate() != null
        and: "the status of the question clarification was set to answered"
        clarificationQuestionRepository.count() == 1L
        def clarificationQuestionResult = clarificationQuestionRepository.findAll().get(0)
        clarificationQuestionResult.getStatus().name() == ClarificationQuestion.Status.ANSWERED.name()
        !clarificationQuestionResult.getNeedClarification()
        clarificationQuestionResult.getResponses().size() == 1L
        and: "responses list of the teacher"
        teacher.getClarificationResponses().size() == 1L
    }

    def 'two teachers clarify the same student'() {
        given: "create clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())
        and: "create a second teacher"
        def second_teacher = new User()
        second_teacher.setKey(2)
        second_teacher.setRole(User.Role.TEACHER)
        userRepository.save(second_teacher)

        when:
        clarificationService.answerClarification(clarificationQuestion.getId(), teacher.getId(), clarificationResponseDto)
        clarificationService.answerClarification(clarificationQuestion.getId(), second_teacher.getId(), clarificationResponseDto)

        then: "the student is clarified by both teachers"
        clarificationResponseRepository.count() == 2L
        def firstTeacherResult = clarificationResponseRepository.findAll().get(0)
        firstTeacherResult.getTeacherResponse() == TEACHER_RESPONSE
        firstTeacherResult.getResponseDate() != null
        def secondTeacherResult = clarificationResponseRepository.findAll().get(1)
        secondTeacherResult.getTeacherResponse() == TEACHER_RESPONSE
        secondTeacherResult.getResponseDate() != null
        and: "the status of the question clarification was set to answered"
        clarificationQuestionRepository.count() == 1L
        def clarificationQuestionResult = clarificationQuestionRepository.findAll().get(0)
        clarificationQuestionResult.getStatus().name() == ClarificationQuestion.Status.ANSWERED.name()
        !clarificationQuestionResult.getNeedClarification()
        clarificationQuestionResult.getResponses().size() == 2L
        and: "responses list of both teachers"
        teacher.getClarificationResponses().size() == 1L
        second_teacher.getClarificationResponses().size() == 1L
    }

    def 'the user is not a teacher'() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())
        and: "create a student"
        def student = new User()
        student.setKey(2)
        student.setRole(User.Role.STUDENT)
        userRepository.save(student)

        when:
        clarificationService.answerClarification(clarificationQuestion.getId(), student.getId(), clarificationResponseDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_IS_NOT_A_TEACHER
    }

    @Unroll("nonexistent objects: #clarificationQuestionId | #teacherId || errorMessage")
    def "clarification/teacher does not exist"() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())
        and: "ids"
        def cQuestionId = clarificationQuestionId == UNEXISTENT_ID ? UNEXISTENT_ID : clarificationQuestion.getId()
        def tId = teacherId = teacherId == UNEXISTENT_ID ? UNEXISTENT_ID : teacher.getId()

        when:
        clarificationService.answerClarification(cQuestionId, tId, clarificationResponseDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        clarificationQuestionId | teacherId     || errorMessage
        UNEXISTENT_ID           | EXISTENT_ID   || QUESTION_CLARIFICATION_NOT_FOUND
        EXISTENT_ID             | UNEXISTENT_ID || USER_NOT_FOUND
    }

    @Unroll("invalid arguments: #clarificationQuestionId | #teacherId | #teacherResponse || errorMessage")
    def "invalid input values"() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        and: "input to test"
        def cQuestionId = clarificationQuestionId == null ? null : clarificationQuestion.getId()
        def tId = teacherId = teacherId == null ? null : teacher.getId()
        clarificationResponseDto.setTeacherResponse(teacherResponse)

        when:
        clarificationService.answerClarification(cQuestionId, tId, clarificationResponseDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        clarificationQuestionId | teacherId   | teacherResponse  || errorMessage
        null                    | EXISTENT_ID | TEACHER_RESPONSE || CLARIFICATION_ID_IS_NULL
        EXISTENT_ID             | null        | TEACHER_RESPONSE || USER_ID_IS_NULL
        EXISTENT_ID             | EXISTENT_ID | null             || RESPONSE_CONTENT
        EXISTENT_ID             | EXISTENT_ID | "   "            || RESPONSE_CONTENT
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
