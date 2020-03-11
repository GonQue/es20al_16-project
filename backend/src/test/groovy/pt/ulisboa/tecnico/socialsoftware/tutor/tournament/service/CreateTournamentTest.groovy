package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_MISSING_DATA

class CreateTournamentTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String TOURNAMENT_NAME = "Tournament name"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME= "topic name"
    public static final int NUMBER_OF_QUESTIONS = 5
    
    def tournamentService
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


    def setup(){
        tournamentService = new TournamentService()

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.setId(1)

        user = new User()
        user.setId(1)
        user.setKey(1)
        user.setRole(User.Role.STUDENT)
        user.addCourse(courseExecution)
        courseExecution.addUser(user)


        userDto = new UserDto(user)
        tournamentDto.setCreator(userDto)

        startDate = LocalDateTime.now()
        endDate = LocalDateTime.now().plusDays(1)
        tournamentDto.setStartDate(startDate.format(formatter))
        tournamentDto.setEndDate(endDate.format(formatter))
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)

        quizDto = new QuizDto()
        quizDto.setKey(1)
        tournamentDto.setQuiz(quizDto)

        //TOPICS
        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_NAME)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_NAME)
        //topicOne = new Topic(course, topicDtoOne)
        //topicTwo = new Topic(course, topicDtoTwo)

        tournamentDto.setTopics(new ArrayList<>(Arrays.asList(topicDtoOne, topicDtoTwo)))
    }

    def "create a tournament with 2 topics"(){
        // create a tournament with a name, creator, start and end time, 2 topics, 5 questions,
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)

        when:
        def result = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        courseExecution.getId() != null
        result.getName() == TOURNAMENT_NAME
        result.getId() == 1
        result.getKey() == 1
        result.getUser.getKey() == 1
        result.getStartDate().format(formatter) == startDate.format(formatter)
        result.getEndDate().format(formatter) == endDate.format(formatter)
        result.getTopics().size() == 2
        result.getTopics().contains(topicOne)
        result.getTopics().contains(topicTwo)
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.getQuiz().getKey()== 1
        
    }

    def "create tournament with 0 topics"(){
        // create a tournament with 0 topics
        given:"a tournament with 0 topics"
        tournamentDto.setName(TOURNAMENT_NAME)
        tournamentDto.setTopics(new ArrayList<>(Arrays.asList()))

        when:
        def result = tournamentService.createTournament(courseExecution.getId(), tournamentDto)

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
        tournamentDto.setCreator(userDto)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        thrown(TutorException)
        //def exception = thrown(TutorException)
        //exception.getErrorMessage() == QUESTION_MISSING_DATA
    }

    def "student is not in course execution of tournament"(){
        //throw exception
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a student not in the course execution"
        def user2 = new User()
        user2.setId(2)
        user2.setRole(User.Role.STUDENT)
        def userDto2 = new UserDto(user2)
        tournamentDto.setCreator(userDto2)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        thrown(TutorException)


    }

    def "End time before start time"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a end time before the start time"
        endDate = startDate.minusDays(1)
        tournamentDto.setEndDate(endDate.format(formatter))

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        thrown(TutorException)
    }

    def "create tournament with 0 questions"(){
        given:"a tournament with 0 questions"
        tournamentDto.setName(TOURNAMENT_NAME)
        tournamentDto.setNumberOfQuestions(0)

        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)

        then:
        thrown(TutorException)
    }

    
    def "tournament name is empty"(){
        given:"a tournament with empty name"
        tournamentDto.setName(null)
        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)
        then:
        thrown(TutorException)
    } 
    
    def "tournament name is blank"(){
        given:"a tournament with blank name"
        tournamentDto.setName("  ")
        when:
        tournamentService.createTournament(courseExecution.getId(), tournamentDto)
        then:
        thrown(TutorException)
    }

}
