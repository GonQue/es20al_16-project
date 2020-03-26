package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class CreateTournamentsServiceSpockPerformanceTest extends Specification {

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    TopicRepository topicRepository

    def "performance test to create 100000 tournaments"() {
        given:"a course execution"
        def course = new Course("COURSE_NAME", Course.Type.TECNICO)
        def courseExecution = new CourseExecution(course, "ACRONYM", "ACADEMIC_TERM", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a tournament dto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setName("TOURNAMENT 1 TESTE PERFORMANCE")
        def user = new User('name', "STUDENT_USERNAME", 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)

        def quiz = new Quiz()
        quiz.setKey(1)
        quizRepository.save(quiz)
        def quizDto = new QuizDto(quiz, false)
        tournamentDto.setQuiz(quizDto)

        def topicOne = new Topic()
        topicOne.setName("TOPIC_NAME_1")
        topicRepository.save(topicOne)
        def TopicDto1 = new TopicDto(topicOne)

        def topicTwo = new Topic()
        topicTwo.setName("TOPIC_NAME_2")
        topicRepository.save(topicTwo)
        def TopicDto2 = new TopicDto(topicTwo)

        def topicThree = new Topic()
        topicThree.setName("TOPIC_NAME_3")
        topicRepository.save(topicThree)
        def TopicDto3 = new TopicDto(topicThree)

        tournamentDto.setTopics(new ArrayList<>(Arrays.asList(TopicDto1, TopicDto2, TopicDto3)))
        tournamentDto.setNumberOfQuestions(2)
        when:
        //1.upto(100000
        1.upto(1, { tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)})

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
