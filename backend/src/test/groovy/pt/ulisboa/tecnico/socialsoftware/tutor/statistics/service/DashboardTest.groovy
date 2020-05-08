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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification


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
    QuestionRepository questionRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def course
    def courseExecution
    def student
    def teacher
    def clarificationQuestion1
    def clarificationQuestion2

    def setup(){
        student = new User("student", "student", 1, User.Role.STUDENT)
        student.setPublicDashboard(true)
        userRepository.save(student)

        teacher = new User("teacher", "teacher", 2, User.Role.TEACHER)
        userRepository.save(teacher)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        clarificationQuestion1 = new ClarificationQuestion()
        clarificationQuestion1.setStudent(student)
        clarificationQuestion1.setAvailableToOtherStudents(true)
        student.addClarificationQuestion(clarificationQuestion1)
        clarificationQuestionRepository.save(clarificationQuestion1)

        clarificationQuestion2 = new ClarificationQuestion()
        clarificationQuestion2.setStudent(student)
        clarificationQuestionRepository.save(clarificationQuestion2)

        student.addClarificationQuestion(clarificationQuestion2)
        student.addCourse(courseExecution)
        teacher.addCourse(courseExecution)

        // proposed questions
        def proposedQuestion1 = createQuestion()
        proposedQuestion1.setEvaluation(ProposedQuestion.Evaluation.APPROVED)
        student.addProposedQuestion(proposedQuestion1)

        def proposedQuestion2 = createQuestion()
        proposedQuestion2.setEvaluation(ProposedQuestion.Evaluation.AVAILABLE)
        student.addProposedQuestion(proposedQuestion2)

        def proposedQuestion3 = createQuestion()
        proposedQuestion3.setEvaluation(ProposedQuestion.Evaluation.REJECTED)
        proposedQuestion3.setJustification('Justification')
        student.addProposedQuestion(proposedQuestion3)

        //tournament
        def tournament1 = new Tournament()
        tournament1.setName(TOURNAMENT_NAME)
        tournament1.setCourseExecution(courseExecution)

        tournament1.setCreator(student)
        tournament1.addStudent(student)
        student.enrollTournament(tournament1)
        student.addTournament(tournament1)
        tournamentRepository.save(tournament1)

        def tournament2 = new Tournament()
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

        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("title")
        quiz.setType(Quiz.QuizType.TOURNAMENT.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setSeries(1)
        quizRepository.save(quiz)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        courseExecution.addQuiz(quiz)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswer.setUser(student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(1)
        questionAnswer.setOption(option)
        questionAnswer.setSequence(0)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
    }

    def 'check student clarification stats'(){
        when:
        StatsDto stats = statsService.getStats(student.getId(), courseExecution.getId())
        then: "the stats show two clarifications and one of them is public"
        stats.getTotalClarificationQuestions() == 2
        stats.getTotalPublicClarificationQuestions() == 1
        stats.getTotalProposedQuestions() == 3
        stats.getTotalApprovedProposedQuestions() == 2
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

    def createQuestion() {
        def questionDto = new QuestionDto()
        questionDto.setTitle("QUESTION_TITLE")
        questionDto.setContent("QUESTION_CONTENT")
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent("OPTION_CONTENT")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        def question = new Question(course, questionDto)
        questionRepository.save(question)

        def proposedQuestion = new ProposedQuestion()
        proposedQuestion.setStudent(student)
        proposedQuestion.setQuestion(question)
        proposedQuestion.setTeacher(teacher)
        proposedQuestionRepository.save(proposedQuestion)
        return proposedQuestion
    }

    @TestConfiguration
    static class StatsServiceImplTestContextConfiguration {

        @Bean
        StatsService clarificationService() {
            return new StatsService()
        }
    }
}