package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_USERNAME
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_CLOSED

@DataJpaTest
class StudentEnrollTournamentTest extends Specification {

    public static final String COURSE_NAME1 = "Course name 1"
    public static final String COURSE_NAME2 = "Course name 2"
    public static final String TOURNAMENT_NAME = "Tournament name"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String USERNAME = "username"
    public static final String TOPIC_NAME_1= "topic name 1"
    public static final String TOPIC_NAME_2= "topic name 2"
    public static final String QUESTION_NAME_1 = "question 1"
    public static final String QUESTION_NAME_2 = "question 2"
    public static final String QUESTION_NAME_3 = "question 3"

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

    @Autowired
    QuestionRepository questionRepository

    def tournament
    def tournamentDto
    def tournamentId
    def course1
    def courseExecution1
    def user
    def user2
    def userDto
    def user2Dto
    def formatter
    def topicOne
    def topicDtoOne
    def topicTwo
    def topicDtoTwo
    def questionOne
    def questionTwo
    def questionThree
    def questions

    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        course1 = new Course(COURSE_NAME1, Course.Type.TECNICO)
        courseRepository.save(course1)

        courseExecution1 = new CourseExecution(course1, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(courseExecution1)

        tournament = new Tournament()
        tournament.setName(TOURNAMENT_NAME)
        tournament.setStatus(Tournament.Status.CREATED)
        tournament.setCourseExecution(courseExecution1)
        tournamentRepository.save(tournament)
        tournamentId=tournament.getId()

        user = new User()
        user.setKey(1)
        user.setRole(User.Role.STUDENT)
        user.setUsername(USERNAME)
        userRepository.save(user)

        userDto = new UserDto(user)

        tournament.setCreator(user)
        tournament.setStartDate(LocalDateTime.now())
        tournament.setEndDate(LocalDateTime.now().plusDays(1))
        tournament.setNumberOfQuestions(1)

        topicOne = new Topic()
        topicOne.setName(TOPIC_NAME_1)
        topicRepository.save(topicOne)
        tournament.setTopics(new HashSet<Topic>(Arrays.asList(topicOne)))

        questionOne = new Question()
        questionOne.setKey(1)
        questionOne.setStatus(Question.Status.AVAILABLE)
        questionOne.setCourse(course1)
        questionOne.setTitle(QUESTION_NAME_1)
        course1.addQuestion(questionOne)
        questionOne.addTopic(topicOne)
        topicOne.addQuestion(questionOne)
        topicDtoOne = new TopicDto(topicOne);
        questionRepository.save(questionOne)


        topicTwo = new Topic()
        topicTwo.setName(TOPIC_NAME_2)
        topicRepository.save(topicTwo)

        questionTwo = new Question()
        questionTwo.setKey(2)
        questionTwo.setStatus(Question.Status.AVAILABLE)
        questionTwo.setCourse(course1)
        questionTwo.setTitle(QUESTION_NAME_2)
        course1.addQuestion(questionTwo)
        questionTwo.addTopic(topicTwo)
        topicTwo.addQuestion(questionTwo)
        topicDtoTwo = new TopicDto(topicTwo);
        questionRepository.save(questionTwo)

        questionThree = new Question()
        questionThree.setKey(3)
        questionThree.setStatus(Question.Status.AVAILABLE)
        questionThree.setCourse(course1)
        questionThree.setTitle(QUESTION_NAME_3)
        course1.addQuestion(questionThree)
        questionThree.addTopic(topicTwo)
        topicTwo.addQuestion(questionThree)
        topicDtoTwo = new TopicDto(topicTwo);
        questionRepository.save(questionThree)

        tournament.setStatus(Tournament.Status.STARTED)
        tournamentDto = new TournamentDto(tournament)

        questions = new ArrayList<Question>(Arrays.asList(questionOne, questionTwo, questionThree))

        List<TopicDto> topicsDto = new ArrayList<TopicDto>();
        topicsDto.add(topicDtoOne);

        tournamentDto.setTopics(topicsDto);

    }

    def "enroll student in the tournament"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a student in a course execution"
        user.addCourse(courseExecution1)

        when:
        def result = tournamentService.enrollStudent(tournamentId, user.getId())

        then:
        result.getName()==TOURNAMENT_NAME
        result.getEnrolled()!=null
        result.getEnrolled().size()==1
        result.getEnrolled().get(0)==USERNAME

    }

    def "student enrolls in a tournament that has students enrolled already and generates quiz"(){
        given:"a closed tournament"
        tournamentDto.setName(TOURNAMENT_NAME)

        and:"a student in a course execution"
        user.addCourse(courseExecution1)
        userDto = new UserDto(user)

        and: "another student enrolled in the tournament"
        user2 = new User()
        user2.setKey(2)
        user2.setRole(User.Role.STUDENT)
        user2.addCourse(courseExecution1)
        userRepository.save(user2)
        user2Dto = new UserDto(user2)
        tournament.getEnrolled().add(user2)
        tournamentDto.setEnrolled(new ArrayList<>(Arrays.asList(user2Dto)))
        tournamentRepository.save(tournament)

        when:
        def result = tournamentService.enrollStudent(tournamentId, user.getId())

        then:
        result.getEnrolled()!=null
        result.getEnrolled().size()==2
        result.getEnrolled().stream().anyMatch({ u -> (u == user.getUsername()) })
        result.getEnrolled().stream().anyMatch({ u -> (u == user2Dto.getUsername()) })

        tournament.getQuiz().getQuizQuestions().size()==1
        tournament.getQuiz().getQuizQuestions()[0].getQuestion().getTitle() == QUESTION_NAME_1

    }

    def "student enrolls in a tournament and generates quiz with 2 topics and 3 questions"(){
        given:"a closed tournament"
        tournamentDto.setName(TOURNAMENT_NAME)

        and:"a student in a course execution"
        user.addCourse(courseExecution1)
        userDto = new UserDto(user)

        and: "another student enrolled in the tournament"
        user2 = new User()
        user2.setKey(2)
        user2.setRole(User.Role.STUDENT)
        user2.addCourse(courseExecution1)
        userRepository.save(user2)
        user2Dto = new UserDto(user2)
        tournament.getEnrolled().add(user2)
        tournamentDto.setEnrolled(new ArrayList<>(Arrays.asList(user2Dto)))
        tournamentRepository.save(tournament)

        and: "tournament with 2 topics"
        tournament.setNumberOfQuestions(3);
        tournament.setTopics(new HashSet<Topic>(Arrays.asList(topicOne, topicTwo)))

        when:
        def result = tournamentService.enrollStudent(tournamentId, user.getId())

        then:
        tournament.getQuiz().getQuizQuestions().size()==3
        questions.contains(tournament.getQuiz().getQuizQuestions()[0].getQuestion())
        questions.contains(tournament.getQuiz().getQuizQuestions()[1].getQuestion())
        questions.contains(tournament.getQuiz().getQuizQuestions()[2].getQuestion())
    }


    def "user enrolling is not a student"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"teacher"
        user.setRole(User.Role.TEACHER)

        when:
        tournamentService.enrollStudent(tournamentId, user.getId())
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_ENROLLED_NOT_STUDENT
    }

    def "student already in tournament"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"student enrolled in the tournament"
        user.addCourse(courseExecution1)
        tournament.getEnrolled().add(user)
        tournamentDto.setEnrolled(new ArrayList<>(Arrays.asList(userDto)))

        when:
        tournamentService.enrollStudent(tournamentId, user.getId())
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_ALREADY_ENROLLED
    }

    def "student not in course execution of tournament"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a student not in the same course execution"
        Course course2 = new Course(COURSE_NAME2, Course.Type.TECNICO)
        courseRepository.save(course2)
        CourseExecution courseExecution2 = new CourseExecution(course2, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)
        user.addCourse(courseExecution2)

        when:
        tournamentService.enrollStudent(tournamentId, user.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_IN_COURSE_EXECUTION
    }

    def "tournament is closed"(){
        given:"a closed tournament"
        tournament.setStatus(Tournament.Status.CLOSED)
        tournamentDto = new TournamentDto(tournament)


        and:"a student in a course execution"
        user.addCourse(courseExecution1)

        when:
        tournamentService.enrollStudent(tournamentId, user.getId())
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_CLOSED


    }

    @Unroll("Invalid arguments: #username || errorMessage")
    def "invalid input values"(){
        given: "a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and: "a user"
        user.addCourse(courseExecution1)
        user.setUsername(username)

        when:
        tournamentService.enrollStudent(tournamentId, user.getId())

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        username        || errorMessage
        null            || INVALID_USERNAME
        "   "           || INVALID_USERNAME
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