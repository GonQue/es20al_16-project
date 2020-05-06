package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification


@DataJpaTest
class DashboardTest extends Specification {
    public static final String CONTENT = "clarificationQuestion content"
    @Autowired
    StatsService statsService

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course
    def courseExecution
    def student
    def clarificationQuestion1
    def clarificationQuestion2

    def setup(){
        student = new User()
        student.setKey(1)
        student.setRole(User.Role.STUDENT)

        clarificationQuestion1 = new ClarificationQuestion()
        clarificationQuestion1.setStudent(student)
        clarificationQuestion1.setContent(CONTENT)
        clarificationQuestion1.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
        clarificationQuestion1.setAvailableToOtherStudents(true)
        student.addClarificationQuestion(clarificationQuestion1)

        clarificationQuestion2 = new ClarificationQuestion()
        clarificationQuestion2.setStudent(student)
        clarificationQuestion2.setContent(CONTENT)
        clarificationQuestion2.setStatus(ClarificationQuestion.Status.NOT_ANSWERED)
        student.addClarificationQuestion(clarificationQuestion2)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        student.addCourse(courseExecution)

        userRepository.save(student)
        clarificationQuestionRepository.save(clarificationQuestion1)
        clarificationQuestionRepository.save(clarificationQuestion2)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        }

    def 'check student clarification stats'(){
        when:
        StatsDto stats = statsService.getStats(student.getId(), courseExecution.getId())
        then: "the stats show two clarifications and one of them is public"
        stats.getTotalClarificationQuestions() == 2
        stats.getTotalPublicClarificationQuestions() == 1
    }

@TestConfiguration
static class StatsServiceImplTestContextConfiguration {

    @Bean
    StatsService clarificationService() {
        return new StatsService()
    }
}
}
