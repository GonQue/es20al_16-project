package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import java.time.format.DateTimeFormatter

import java.time.LocalDateTime


@DataJpaTest
class DeleteTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String TOURNAMENT_NAME = "Tournament name"
    public static final String STUDENT_USERNAME= "student username test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final int NUMBER_OF_QUESTIONS = 5

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

    def tournament
    def tournamentDto
    def tournamentId
    def course
    def courseExecution
    def user
    def userDto
    def creatorId
    def startDate
    def formatter

    def setup() {
        tournamentDto = new TournamentDto()
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setName(TOURNAMENT_NAME)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', STUDENT_USERNAME, 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        userDto = new UserDto(user)

        tournament = new Tournament()
        tournament.setName(TOURNAMENT_NAME)
        tournament.setStatus(Tournament.Status.CREATED)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)



        tournament.setStartDate(LocalDateTime.now().plusDays(3))

    }
    def "creator delete created tournament"() {
        given: "a tournament"
        tournamentId=tournament.getId()
        and: "a creator"
        tournament.setCreator(user)
        creatorId=user.getId()
        when:
        tournamentService.deleteTournament(tournamentId, creatorId)

        then:
        tournamentRepository.existsById(tournamentId)==false
    }

    def "cannot delete tournament after it started"(){
        given: "a open tournament"
        tournamentId=tournament.getId()
        tournament.setStartDate(LocalDateTime.now())
        and: "a creator"
        tournament.setCreator(user)
        creatorId=user.getId()
        when:
        tournamentService.deleteTournament(tournamentId, creatorId)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_STARTED
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
        @Bean
        QuizService quizService(){
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}
