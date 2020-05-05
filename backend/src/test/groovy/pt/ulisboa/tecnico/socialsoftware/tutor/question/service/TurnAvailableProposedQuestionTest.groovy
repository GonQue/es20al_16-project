package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class TurnAvailableProposedQuestionTest extends Specification {

    @Autowired
    QuestionService questionService

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

        question = new Question(course, questionDto)
        questionRepository.save(question)

        proposedQuestion = new ProposedQuestion()
        proposedQuestion.setStudent(student)
        proposedQuestion.setQuestion(question)
        proposedQuestionRepository.save(proposedQuestion)
    }

    def 'turn an approved proposed question available' (){
        given: "an approved question"
        proposedQuestion.setTeacher(teacher)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED)

        def approvedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        approvedQuestionDto.setJustification(" ")

        and: "turn available"
        approvedQuestionDto.setEvaluation(ProposedQuestion.Evaluation.AVAILABLE.name())

        when:
        def result = proposedQuestionService.turnAvailable(approvedQuestionDto)

        then:
        result.getEvaluation() == ProposedQuestion.Evaluation.AVAILABLE.name()
        result.getQuestion().getStatus() == Question.Status.AVAILABLE.name()
    }

    def 'turn a rejected proposed question available' (){
        given: "an rejected question"
        proposedQuestion.setTeacher(teacher)
        proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.REJECTED)

        def approvedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        approvedQuestionDto.setJustification("JUSTIFICATION")

        and: "turn available"
        approvedQuestionDto.setEvaluation(ProposedQuestion.Evaluation.AVAILABLE.name())

        when:
        proposedQuestionService.turnAvailable(approvedQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == PROPQUESTION_NOT_APPROVED

    }

    @TestConfiguration
    static class TeacherEvaluateTestContextConfiguration {
        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        ProposedQuestionService proposedQuestionService() {
            return new ProposedQuestionService()
        }
    }
}