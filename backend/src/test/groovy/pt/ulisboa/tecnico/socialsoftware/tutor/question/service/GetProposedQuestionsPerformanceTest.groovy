package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class GetProposedQuestionsPerformanceTest extends Specification {

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

    def course
    def student
    def teacher

    def setup(){
        student = new User("student", "student", 1, User.Role.STUDENT)
        userRepository.save(student)

        teacher = new User("teacher", "teacher", 2, User.Role.TEACHER)
        userRepository.save(teacher)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        teacher.setCourseExecutions(courseExecSet)
        courseExecutionRepository.save(courseExecution)
    }

    def "performance testing to get 2000 proposed questions 20000 times"() {
        given: "2000 proposed questions"

        1.upto(1, {
            def propQuestion = new ProposedQuestion()
            propQuestion.setQuestion(createQuestion(it))
            propQuestion.setStudent(student)
            propQuestion.setTeacher(teacher)
            propQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED)
            propQuestion.setJustification(" ")
            proposedQuestionRepository.save(propQuestion)
            student.addProposedQuestion(propQuestion)
        })

        when:
        1.upto(1, {
            proposedQuestionService.getStudentProposedQuestions(student.getId())
        })

        then:
        true
    }

    def "performance testing to get 2000 proposed questions from course 20000 times"() {
        given: "2000 proposed questions"

        1.upto(1, {
            def propQuestion = new ProposedQuestion()
            propQuestion.setQuestion(createQuestion(it))
            propQuestion.setStudent(student)
            propQuestion.setTeacher(teacher)
            propQuestion.setEvaluation(ProposedQuestion.Evaluation.APPROVED)
            propQuestion.setJustification(" ")
            proposedQuestionRepository.save(propQuestion)
            student.addProposedQuestion(propQuestion)
            course.addProposedQuestion(propQuestion)
        })

        when:
        1.upto(1, {
            proposedQuestionService.getCourseProposedQuestions(course.getId())
        })

        then:
        true
    }

    def createQuestion(key){
        def questionDto = new QuestionDto()
        questionDto.setKey(key)
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
        return question
    }

    @TestConfiguration
    static class GetProposedQuestionTestContextConfiguration {
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