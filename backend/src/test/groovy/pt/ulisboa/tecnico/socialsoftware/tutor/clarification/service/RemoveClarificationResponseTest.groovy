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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class RemoveClarificationResponseTest extends Specification{
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
    def teacher
    def clarificationResponse

    def setup() {
        teacher = new User()
        teacher.setKey(1)
        teacher.setRole(User.Role.TEACHER)

        clarificationQuestion = new ClarificationQuestion()
        clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)

        clarificationResponse = new ClarificationResponse()
        clarificationResponse.setTeacher(teacher)
        clarificationResponse.setTeacherResponse(TEACHER_RESPONSE)
        clarificationResponse.setClarificationQuestion(clarificationQuestion)
        clarificationQuestion.addResponse(clarificationResponse)

        userRepository.save(teacher)
        clarificationQuestionRepository.save(clarificationQuestion)
        clarificationResponseRepository.save(clarificationResponse)
    }

    def 'remove a clarification response'() {
        when: "the service is called"
        clarificationService.removeClarificationResponse(clarificationResponse.getId())

        then: "the response is removed"
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
