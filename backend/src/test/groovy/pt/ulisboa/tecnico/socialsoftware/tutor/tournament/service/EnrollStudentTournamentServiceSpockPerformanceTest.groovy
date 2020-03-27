package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class EnrollStudentTournamentServiceSpockPerformanceTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOURNAMENT_NAME1 = "Tournament name1"

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    def course
    def courseExecution

    def "performance test to enroll students in one tournament"() {
        given:"a course execution"
        course = new Course("COURSE_NAME", Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, "ACRONYM", "ACADEMIC_TERM", Course.Type.TECNICO)

        and: "1000 students to enroll in a tournament"
        List<User> users = new ArrayList<User>()
        for(int i=0; i<1001; i++) {
            def user = new User()
            user.setKey(i)
            def courseSet = new HashSet<CourseExecution>(Arrays.asList(courseExecution))
            //courseSet.add(courseExecution)
            user.setRole(User.Role.STUDENT)
            user.setCourseExecutions(courseSet)
            user.setUsername("student"+i)
            userRepository.save(user)
            users.add(user)
        }

        def quiz = new Quiz()
        quiz.setKey(1)
        quizRepository.save(quiz)

        def tournament = new Tournament()
        tournament.setName(TOURNAMENT_NAME1)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartDate(LocalDateTime.now())
        tournament.setEndDate(LocalDateTime.now().plusDays(1))
        tournament.setStatus(Tournament.Status.CREATED)
        tournament.setQuiz(quiz)
        tournament.setNumberOfQuestions(5)
        tournament.setCreator(users[0])

        tournamentRepository.save(tournament)
        courseExecutionRepository.save(courseExecution)
        when:
        0.upto(1000, { tournamentService.enrollStudent(tournament.getId(), users[it].getId())})

        then:
        true

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration{

        @Bean
        TournamentService tournamentService(){
            return new TournamentService()
        }
    }
}
