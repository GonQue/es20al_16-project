package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationResponseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreateClarificationResponsePerformanceTest extends Specification {

    public static final String TEACHER_RESPONSE = "teacher response"

    @Autowired
    ClarificationService clarificationService

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository

    @Autowired
    UserRepository userRepository

    def clarificationQuestion
    def clarificationResponse
    def teacher

    def setup(){
        teacher = new User()
        teacher.setKey(1)
        teacher.setRole(User.Role.TEACHER)
        userRepository.save(teacher)

        clarificationQuestion = new ClarificationQuestion()
        clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
        clarificationQuestionRepository.save(clarificationQuestion)

        clarificationResponse = new ClarificationResponse()
        clarificationResponse.setTeacher(teacher)
        clarificationResponse.setTeacherResponse(TEACHER_RESPONSE)
        clarificationResponse.setClarificationQuestion(clarificationQuestion)
        clarificationResponseRepository.save(clarificationResponse)
    }

    def "performance testing to create 10000 clarification responses"() {
        given: "a clarificationResponseDto"
        def clarificationResponseDto = new ClarificationResponseDto()
        clarificationResponseDto.setId(clarificationResponse.getId())
        clarificationResponseDto.setTeacherResponse(clarificationResponse.getTeacherResponse())

        when:
        1.upto(1, {clarificationService.answerClarification(clarificationResponse.getClarificationQuestion().getId(), clarificationResponse.getTeacher().getId(),clarificationResponseDto)})

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
