package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime


@DataJpaTest
class DashboardTest extends Specification {

    public static final String TOURNAMENT_NAME = "Tournament name"

    @Autowired
    StatsService statsService

    @Autowired
    ClarificationQuestionRepository clarificationQuestionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    def course
    def courseExecution
    def student
    def clarificationQuestion1
    def clarificationQuestion2
    def tournament1
    def tournament2
    def quiz
    def quizQuestion
    def questionAnswer
    def option
    def quizAnswer
    def question


    def setup(){
        student = new User()
        student.setKey(1)
        student.setRole(User.Role.STUDENT)
        student.setPublicDashboard(true)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        clarificationQuestion1 = new ClarificationQuestion()
        clarificationQuestion1.setStudent(student)
        clarificationQuestion1.setAvailableToOtherStudents(true)
        student.addClarificationQuestion(clarificationQuestion1)

        clarificationQuestion2 = new ClarificationQuestion()
        clarificationQuestion2.setStudent(student)

        student.addClarificationQuestion(clarificationQuestion2)
        student.addCourse(courseExecution)

        //tournament
        tournament1 = new Tournament()
        tournament1.setName(TOURNAMENT_NAME)
        tournament1.setCourseExecution(courseExecution)

        tournament1.setCreator(student)
        tournament1.addStudent(student)
        student.enrollTournament(tournament1)
        student.addTournament(tournament1)
        tournamentRepository.save(tournament1)

        tournament2 = new Tournament()
        tournament2.setName(TOURNAMENT_NAME)
        tournament2.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament2)

        tournament2.setCreator(student)
        student.addTournament(tournament2)


        def question = new Question()
        question.setCourse(course)
        question.setKey(1)
        question.setTitle("title")
        question.setContent("question")
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        def option = new Option()
        option.setContent("option")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestion(question)
        optionRepository.save(option)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("title")
        quiz.setType(Quiz.QuizType.TOURNAMENT.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setSeries(1)
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        courseExecution.addQuiz(quiz)

        quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswer.setUser(student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(1)
        questionAnswer.setOption(option)
        questionAnswer.setSequence(0)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        userRepository.save(student)
        clarificationQuestionRepository.save(clarificationQuestion1)
        clarificationQuestionRepository.save(clarificationQuestion2)
        courseRepository.save(course)
        courseExecutionRepository.save(courseExecution)
        }

    def 'check student clarification stats'(){
        when:
        StatsDto stats = statsService.getStats(student.getId(), courseExecution.getId())
        then: "the stats show two clarifications and one of them is public"
        stats.getTotalClarificationQuestions() == 2
        stats.getTotalPublicClarificationQuestions() == 1
        stats.getTotalTournamentsCreated() == 2
        stats.getTotalTournamentsJoined() == 1
        stats.getTotalPoints() == 1
        stats.getTournamentCorrectAnswersPerc() == 100
    }

    def 'the dashboard is public'(){
        when:
        StatsDto stats = statsService.getStats(student.getId(), courseExecution.getId())
        then: 'the dashboard is shown as public'
        stats.getPublicDashboard()
    }

    def 'change dashboard from private to public'(){
        when: 'toggle dashboard to private'
        statsService.togglePublicDashboard(student.getId())
        StatsDto stats = statsService.getStats(student.getId(), courseExecution.getId())
        then: 'the dashboard is shown as private'
        !stats.getPublicDashboard()
    }

@TestConfiguration
static class StatsServiceImplTestContextConfiguration {

    @Bean
    StatsService clarificationService() {
        return new StatsService()
    }
}
}
