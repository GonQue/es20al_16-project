package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationResponseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationResponse
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetClarificationResponsesPerformanceTest extends Specification{

    public static final String TEACHER_RESPONSE = "teacher response"

    @Autowired
    ClarificationService clarificationService

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository

    @Autowired
    ClarificationResponseRepository clarificationResponseRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to get 10000 clarification responses"() {
        def teacher = new User()
        teacher.setKey(1)
        teacher.setRole(User.Role.TEACHER)
        userRepository.save(teacher)
        def clarificationQuestion = new ClarificationQuestion()
        clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
        clarificationQuestionRepository.save(clarificationQuestion)
        given: "10000 clarification responses"
        1.upto(1, {
            def cR = new ClarificationResponse()
            cR.setTeacher(teacher)
            cR.setClarificationQuestion(clarificationQuestion)
            cR.setTeacherResponse(TEACHER_RESPONSE + it)
            clarificationResponseRepository.save(cR)
        })

        when:
        1.upto(1, {clarificationService.listResponses(clarificationQuestion.getId())})

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
