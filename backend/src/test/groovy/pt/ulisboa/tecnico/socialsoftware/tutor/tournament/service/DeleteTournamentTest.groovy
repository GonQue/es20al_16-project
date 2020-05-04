package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification



@DataJpaTest
class DeleteTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String TOURNAMENT_NAME = "Tournament name"
    public static final String STUDENT_USERNAME= "student username test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

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

    def tournamentDto
    def course
    def courseExecution
    def user
    def userDto

    def setup() {
        tournamentDto = new TournamentDto()

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', STUDENT_USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
        userDto = new UserDto(user)
    }
    def "creator delete created tournament"() {
        given: "a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        TournamentDto tournament = tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)
        and: "a creator"
        tournament.setCreator(user)
        when:
        tournamentService.deleteTournament(tournamentDto.getId())

        then:
        tournamentService.tournamentExists(tournamentDto.getId())==false
    }

    def "cannot delete tournament after it started"(){
        given: "a started tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        TournamentDto tournament = tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)
        tournament.setStatus(Tournament.Status.STARTED as String)
        when:
        tournamentService.deleteTournament(tournamentDto.getId())
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_STARTED
    }


}
