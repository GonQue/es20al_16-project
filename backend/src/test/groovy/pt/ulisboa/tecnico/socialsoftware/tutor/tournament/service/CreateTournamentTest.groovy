package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
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
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NO_CREATOR
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_QUIZ_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NAME_INVALID


@DataJpaTest
class CreateTournamentTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String TOURNAMENT_NAME = "Tournament name"
    public static final String QUIZ_NAME = "Quiz name"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME_1= "topic name 1"
    public static final String TOPIC_NAME_2= "topic name 2"
    public static final String STUDENT_USERNAME= "student username test"
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

    @Autowired
    QuizRepository quizRepository

    @Autowired
    TopicRepository topicRepository

    //def tournamentService
    def tournamentDto
    def formatter
    def course
    def courseExecution
    def user
    def userDto
    def startDate
    def endDate
    def topicDtoOne
    def topicOne
    def topicDtoTwo
    def topicTwo
    def quizDto
    def quiz


    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        tournamentDto = new TournamentDto()

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        courseExecutionRepository.save(courseExecution)

        user = new User('name', STUDENT_USERNAME, 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)

        userDto = new UserDto(user)
        //tournamentDto.setCreator(userDto)

        startDate = LocalDateTime.now()
        endDate = LocalDateTime.now().plusDays(1)
        tournamentDto.setStartDate(startDate.format(formatter))
        tournamentDto.setEndDate(endDate.format(formatter))
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setAvailableDate(LocalDateTime.now().minusDays(1))
        quiz.setCourseExecution(courseExecution)
        quiz.setTitle(QUIZ_NAME)
        quizRepository.save(quiz)

        quizDto = new QuizDto(quiz, false)
        tournamentDto.setQuiz(quizDto)

        //Topics
        topicOne = new Topic()
        topicOne.setName(TOPIC_NAME_1)
        topicTwo = new Topic()
        topicTwo.setName(TOPIC_NAME_2)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        topicDtoOne = new TopicDto(topicOne)
        topicDtoTwo = new TopicDto(topicTwo)
        tournamentDto.setTopics(new ArrayList<>(Arrays.asList(topicDtoOne, topicDtoTwo)))
    }

    def "create a tournament with 2 topics"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)


        when:
        def result = tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)

        then:
        result.getName() == TOURNAMENT_NAME
        result.getStartDate() == startDate.format(formatter)
        result.getEndDate() == endDate.format(formatter)
        result.getTopics().size() == 2
        result.getTopics().contains(topicDtoOne)
        result.getTopics().contains(topicDtoTwo)
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.getQuiz().getKey() == 1
        result.getCreator().getUsername()==STUDENT_USERNAME
        
    }

    def "create tournament with 0 topics"(){
        // create a tournament with 0 topics
        given:"a tournament with 0 topics"
        tournamentDto.setName(TOURNAMENT_NAME)
        tournamentDto.setTopics(new ArrayList<>(Arrays.asList()))

        when:
        def result = tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)

        then:
        result.getTopics() != null
        result.getTopics().size() == 0

    }

    def "tournament creator is not a student"(){
        //throw exception
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a teacher "
        user.setRole(User.Role.TEACHER)
        userDto = new UserDto(user)
        //tournamentDto.setCreator(userDto)
        userRepository.save(user)

        when:
        tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_CREATOR_NOT_STUDENT
    }

    def "student is not in course execution of tournament"(){
        //throw exception
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a student not in the course execution"
        def user2 = new User('name2', "username2", 2, User.Role.STUDENT)
        userRepository.save(user2)
        //def userDto2 = new UserDto(user2)
        //tournamentDto.setCreator(userDto2)

        when:
        tournamentService.createTournament(courseExecution.getId(), user2.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_CREATOR_NOT_ENROLLED
    }



    def "End time before start time"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a end time before the start time"
        endDate = startDate.minusDays(1)
        tournamentDto.setEndDate(endDate.format(formatter))

        when:
        tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_TIME_INVALID
    }


    def "create tournament with 0 questions"(){
        given:"a tournament with 0 questions"
        tournamentDto.setName(TOURNAMENT_NAME)
        tournamentDto.setNumberOfQuestions(0)

        when:
        tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NUMBER_OF_QUESTIONS_INVALID
    }

    @Unroll("Invalid arguments: #name | #hasCreator | #hasQuiz || errorMessage")
    def "invalid input values"(){
        given: "a tournament"
        tournamentDto = new TournamentDto()
        tournamentDto.setName(name)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        createUserCreator(hasCreator)
        createQuiz(hasQuiz)

        when:
        tournamentService.createTournament(courseExecution.getId(), user.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        name            | hasCreator| hasQuiz      || errorMessage
        null            | true      | true      || TOURNAMENT_NAME_INVALID
        "  "            | true      | true      || TOURNAMENT_NAME_INVALID
        TOURNAMENT_NAME | false     | true      || TOURNAMENT_NO_CREATOR
        TOURNAMENT_NAME | true      | false     || TOURNAMENT_QUIZ_NOT_FOUND

    }

    def createUserCreator(hasCreator){
        if(!hasCreator){  user.id=0 }
    }
    def createQuiz(hasQuiz){
        if(hasQuiz){ tournamentDto.setQuiz(quizDto) }
        else{ tournamentDto.setQuiz(null) }
    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration{

        @Bean
        TournamentService tournamentService(){
            return new TournamentService()
        }

    }

}
