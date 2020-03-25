package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
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
import spock.lang.Specification

@DataJpaTest
class TeacherEvaluatesProposedQuestionPerformanceTest extends Specification {

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
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository


    def student
    def teacher
    def course
    def courseExecution
    def proposedQuestion
    def proposedQuestionDto
    def options

    def setup() {
        teacher = new User("teacher", "teacher", 1, User.Role.TEACHER)
        userRepository.save(teacher)

        student = new User("student", "student", 2, User.Role.STUDENT)
        userRepository.save(student)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)
        teacher.setCourseExecutions(courseExecSet)

        def optionDto = new OptionDto()
        optionDto.setContent("OPTION_CONTENT")
        optionDto.setCorrect(true)
        options = new ArrayList<OptionDto>()
        options.add(optionDto)
    }


    def "performance testing to evaluate 1000 proposed questions"() {
        given: "a set of proposed questions"
        def list = new ArrayList<ProposedQuestionDto>()

        and: "10000 proposed questions"
        1.upto(1, {
            def questionDto = new QuestionDto()
            questionDto.setKey(it)
            questionDto.setTitle("QUESTION_TITLE")
            questionDto.setContent("QUESTION_CONTENT")
            questionDto.setStatus(Question.Status.SUBMITTED.name())
            questionDto.setOptions(options)

            def question = new Question(course, questionDto)
            questionRepository.save(question)

            proposedQuestion = new ProposedQuestion()
            proposedQuestion.setStudent(student)
            proposedQuestion.setQuestion(question)
            proposedQuestionRepository.save(proposedQuestion)

            proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
            proposedQuestionDto.setTeacherId(teacher.getId())
            proposedQuestionDto.setEvaluation(ProposedQuestion.Evaluation.AWAITING.name())
            proposedQuestionDto.setJustification(" ")

            list.add(proposedQuestionDto)
        })

        when:
        1.upto(1, {
            proposedQuestionService.teacherEvaluatesProposedQuestion(list.get((it as int) - 1))
        })

        then:
        true
    }

    @TestConfiguration
    static class TeacherEvaluateTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        ProposedQuestionService questionProposalService() {
            return new ProposedQuestionService()
        }
    }

}
