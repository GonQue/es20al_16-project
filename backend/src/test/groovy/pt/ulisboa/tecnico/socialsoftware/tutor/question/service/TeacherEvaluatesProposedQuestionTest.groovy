package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.JUSTIFICATION_IS_BLANK
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.JUSTIFICATION_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_EMPTY
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification


@DataJpaTest
class TeacherEvaluatesProposedQuestionTest extends Specification {
    static final String JUSTIFICATION = "JUSTIFICATION"

    @Autowired
    ProposedQuestionService proposedQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    @Autowired
    QuestionRepository  questionRepository

    @Autowired
    UserRepository userRepository

    def teacher
    def teacherDto
    def course
    def courseExecution
    def question
    def proposedQuestion
    def proposedQuestionDto

    def setup() {
        teacher = new User("teacher", "teacher", 1, User.Role.TEACHER)
        userRepository.save(teacher)
        teacherDto = new UserDto(teacher)

        def student = new User("student", "student", 2, User.Role.STUDENT)
        userRepository.save(student)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)
        teacher.setCourseExecutions(courseExecSet)

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
        questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter))

        question = new Question(course, questionDto)
        questionRepository.save(question)

        proposedQuestion = new ProposedQuestion()
        proposedQuestion.setStudent(student)
        proposedQuestion.setQuestion(question)
        proposedQuestionRepository.save(proposedQuestion)
    }


    def 'the user is not a teacher'() {
        given: "a admin"
        def admin = new User("admin", "admin", 3, User.Role.ADMIN)
        userRepository.save(admin)

        and: "a proposed question"
        proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        proposedQuestionDto.setTeacher(new UserDto(admin))
        proposedQuestionDto.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
        proposedQuestionDto.setJustification(" ")

        when:
        proposedQuestionService.teacherEvaluatesProposedQuestion(proposedQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ACCESS_DENIED
    }


    def 'the teacher teaches the course'() {
        given: "a proposed question"
        proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        proposedQuestionDto.setTeacher(teacherDto)
        proposedQuestionDto.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
        proposedQuestionDto.setJustification(JUSTIFICATION)

        when:
        def result = proposedQuestionService.teacherEvaluatesProposedQuestion(proposedQuestionDto)

        then:
        result.getTeacher().getId() == teacher.getId()
        teacher.getCourseExecutions().size() == 1
        teacher.getCourseExecutions().contains(courseExecution) == true
    }


    def 'the question is approved'() {
        given: "a proposed question"
        proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        proposedQuestionDto.setTeacher(teacherDto)
        proposedQuestionDto.setEvaluation(ProposedQuestion.Evaluation.APPROVED.name())
        proposedQuestionDto.setJustification(" ")

        when:
        def result = proposedQuestionService.teacherEvaluatesProposedQuestion(proposedQuestionDto)

        then:
        result.getEvaluation() == ProposedQuestion.Evaluation.APPROVED.name()
    }


    def 'the proposed question was already approved' (){
        given: "an approved question"
        proposedQuestion.setTeacher(teacher)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED)

        def approvedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        approvedQuestionDto.setJustification(" ")

        and: "a repeated evaluation"
        approvedQuestionDto.setEvaluation(ProposedQuestion.Evaluation.APPROVED.name())
        approvedQuestionDto.setTeacher(teacherDto)

        when:
        def result = proposedQuestionService.teacherEvaluatesProposedQuestion(approvedQuestionDto)

        then:
        result.getEvaluation() == ProposedQuestion.Evaluation.APPROVED.name()
    }


    @Unroll("invalid input: #hasTeacher|#justification|#evaluation|errorMessage")
    def 'invalid input'(){
        given: "a proposed question"
        proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        addTeacher(hasTeacher)
        proposedQuestionDto.setJustification(justification)
        proposedQuestionDto.setEvaluation(evaluation.name())

        when:
        proposedQuestionService.teacherEvaluatesProposedQuestion(proposedQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        hasTeacher | justification | evaluation                             || errorMessage
        false      | JUSTIFICATION | ProposedQuestion.Evaluation.AWAITING   || USER_IS_EMPTY
        true       | "           " | ProposedQuestion.Evaluation.REJECTED   || JUSTIFICATION_IS_BLANK
        true       | null          | ProposedQuestion.Evaluation.REJECTED   || JUSTIFICATION_IS_EMPTY
    }

    def addTeacher(Boolean hasTeacher) {
        if (hasTeacher) {
            def courseExecution = new CourseExecution(course, "ES", "1S", Course.Type.TECNICO)
            courseExecution.addUser(teacher)
            teacher.addCourse(courseExecution)
            proposedQuestionDto.setTeacher(teacherDto)
        }
        else {
            proposedQuestionDto.setTeacher(null)
        }
    }

    @TestConfiguration
    static class TeacherEvaluateTestContextConfiguration {

        @Bean
        ProposedQuestionService proposedQuestionService() {
            return new ProposedQuestionService()
        }
    }
}