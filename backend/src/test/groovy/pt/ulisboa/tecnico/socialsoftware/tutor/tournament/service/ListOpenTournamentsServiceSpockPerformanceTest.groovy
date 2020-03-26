package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification;

import java.time.LocalDateTime;

@DataJpaTest
class ListOpenTournamentsServiceSpockPerformanceTest extends Specification {

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

    def "performance test to list 100000 tournaments"() {
        given:"a course execution"
        course = new Course("COURSE_NAME", Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, "ACRONYM", "ACADEMIC_TERM", Course.Type.TECNICO)

        and: "200 tournament dtos"
        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        def quiz = new Quiz()
        quiz.setKey(1)
        quizRepository.save(quiz)

        Set<Tournament> tournaments = new HashSet<>();
        for(int i=0; i<200; i++){
            def tournament = new Tournament()
            tournament.setName(TOURNAMENT_NAME1)
            tournament.setCourseExecution(courseExecution)
            tournament.setStartDate(LocalDateTime.now())
            tournament.setEndDate(LocalDateTime.now().plusDays(1))
            tournament.setStatus(Tournament.Status.CREATED)
            tournament.setQuiz(quiz)
            tournament.setNumberOfQuestions(5)
            tournament.setCreator(user)
            tournaments.add(tournament)

        }
        courseExecution.setTournaments(tournaments);
        courseExecutionRepository.save(courseExecution)
        when:
        //1.upto(10000
        1.upto(10000, { tournamentService.listOpenTournaments(courseExecution.getId())})

        then:
        courseExecution.getTournaments().size()==200
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
