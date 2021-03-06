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
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
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
class ListOpenTournamentsTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOURNAMENT_NAME1 = "Tournament name1"
    public static final String TOURNAMENT_NAME2 = "Tournament name2"
    public static final String TOURNAMENT_NAME3 = "Tournament name3"

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
    def tournament1
    def tournament2
    def tournament3

    def setup(){
        course = new Course("COURSE_NAME", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "ACRONYM", "ACADEMIC_TERM", Course.Type.TECNICO)

        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quizRepository.save(quiz)

        tournament1 = new Tournament()
        tournament1.setName(TOURNAMENT_NAME1)
        tournament1.setCourseExecution(courseExecution)
        tournament1.setStartDate(LocalDateTime.now())
        tournament1.setStatus(Tournament.Status.CREATED)
        tournament1.setEndDate(LocalDateTime.now().plusDays(1))
        tournament1.setQuiz(quiz)
        tournament1.setNumberOfQuestions(5)
        tournament1.setCreator(user)

        tournament2 = new Tournament()
        tournament2.setName(TOURNAMENT_NAME2)
        tournament2.setCourseExecution(courseExecution)
        tournament2.setStartDate(LocalDateTime.now())
        tournament2.setStatus(Tournament.Status.CREATED)
        tournament2.setEndDate(LocalDateTime.now().plusDays(1))
        tournament2.setQuiz(quiz)
        tournament2.setNumberOfQuestions(5)
        tournament2.setCreator(user)

        tournament3 = new Tournament()
        tournament3.setName(TOURNAMENT_NAME3)
        tournament3.setCourseExecution(courseExecution)
        tournament3.setStartDate(LocalDateTime.now())
        tournament3.setStatus(Tournament.Status.CREATED)
        tournament3.setEndDate(LocalDateTime.now().plusDays(1))
        tournament3.setQuiz(quiz)
        tournament3.setNumberOfQuestions(5)
        tournament3.setCreator(user)



    }

    def "no tournaments"(){
        given:"course execution without tournaments"
        courseExecutionRepository.save(courseExecution)

        when:
        def result = tournamentService.listOpenTournaments(course.getId())
        then:
        result != null
        result.isEmpty();

    }

    def "three open tournaments"(){
        given:"3 open tournaments"
        tournament1.setStatus(Tournament.Status.CREATED)
        tournament2.setStatus(Tournament.Status.CREATED)
        tournament3.setStatus(Tournament.Status.CREATED)
        tournament3.setCourseExecution(courseExecution)

        courseExecution.addTournament(tournament1)
        courseExecution.addTournament(tournament2)
        courseExecution.addTournament(tournament3)
        courseExecutionRepository.save(courseExecution)

        when:
        def result = tournamentService.listOpenTournaments(course.getId())
        then:
        result != null
        result.size() == 3;
        result[0].getName()==TOURNAMENT_NAME1
        result[1].getName()==TOURNAMENT_NAME2
        result[2].getName()==TOURNAMENT_NAME3
    }


    def "only open tournaments on the list"(){
        given:"a created tournament"
        tournament1.setStatus(Tournament.Status.CREATED)
        courseExecution.addTournament(tournament1)
        and:"a started tournament"
        tournament2.setStatus(Tournament.Status.STARTED)
        courseExecution.addTournament(tournament2)
        and:"a closed tournament"
        tournament3.setStatus(Tournament.Status.CLOSED)
        courseExecution.addTournament(tournament3)

        courseExecutionRepository.save(courseExecution)

        when:
        def result = tournamentService.listOpenTournaments(course.getId())
        then:
        result != null
        result.size() == 1;
        result[0].getName()==TOURNAMENT_NAME1
    }



    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration{

        @Bean
        TournamentService tournamentService(){
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
