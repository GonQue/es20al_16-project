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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class GetTournamentQuizTest  extends Specification {

    public static final String COURSE_NAME1 = "Course name 1"
    public static final String TOURNAMENT_NAME = "Tournament name"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String USERNAME = "username"
    public static final String USERNAME_2 = "username 2"
    public static final String TOPIC_NAME_1= "topic name 1"
    public static final String QUESTION_NAME_1 = "question 1"

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
    def userDto
    def formatter
    def topicOne
    def topicDtoOne
    def questionOne
    def questionTwo
    def questionThree
    def questions
    def quiz
    def quizQuestion



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
        user.addCourse(courseExecution1)
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
        topicDtoOne = new TopicDto(topicOne)
        questionRepository.save(questionOne)


        quiz = new Quiz()
        quiz.setCourseExecution(courseExecution1)

        quizQuestion = new QuizQuestion(quiz, questionOne, 1)

        quiz.addQuizQuestion(quizQuestion)
        quizRepository.save(quiz)
        tournament.setQuiz(quiz)

        tournament.setStatus(Tournament.Status.STARTED)
        tournamentDto = new TournamentDto(tournament)

        questions = new ArrayList<Question>(Arrays.asList(questionOne, questionTwo, questionThree))

        List<TopicDto> topicsDto = new ArrayList<TopicDto>();
        topicsDto.add(topicDtoOne);

        tournamentDto.setTopics(topicsDto);
    }

    def "student gets quizStatement from tournament"(){
        given:"a student in the tournament"
        def user2 = new User()
        user2.setKey(2)
        user2.setRole(User.Role.STUDENT)
        user2.setUsername(USERNAME_2)
        user2.addCourse(courseExecution1)
        userRepository.save(user2)
        userDto = new UserDto(user2)
        tournament.setEnrolled(new HashSet<User>(Arrays.asList((user2))))

        and:"a tournament that already started"
        tournament.setStartDate(LocalDateTime.now().minusDays(1))
        tournament.setEndDate(LocalDateTime.now().plusDays(1))

        when:
        def result = tournamentService.getTournamentQuiz(tournament.getId(), user2.getId());

        then:
        //questionRepository.findById(result.getQuestions()[1].getQuestionId())
        result.getQuestions().size() == 1
        result.getQuestions()[0].getQuestionId() == questionOne.getId()
    }

    def "student tries to get tournament that already ended"(){
        given:"a student in the tournament"
        def user2 = new User()
        user2.setKey(2)
        user2.setRole(User.Role.STUDENT)
        user2.setUsername(USERNAME_2)
        user2.addCourse(courseExecution1)
        userRepository.save(user2)
        userDto = new UserDto(user2)
        tournament.setEnrolled(new HashSet<User>(Arrays.asList((user2))))

        and:"a tournament that already ended"
        tournament.setStartDate(LocalDateTime.now().minusDays(2))
        tournament.setEndDate(LocalDateTime.now())

        when:
        def result = tournamentService.getTournamentQuiz(tournament.getId(), user2.getId());

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NO_LONGER_AVAILABLE
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
