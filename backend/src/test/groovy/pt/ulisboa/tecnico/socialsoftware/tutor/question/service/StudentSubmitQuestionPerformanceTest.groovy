package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.ProposedQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification

import java.time.LocalDateTime;

@DataJpaTest
class StudentSubmitQuestionPerformanceTest extends Specification {

    @Autowired
    ProposedQuestionService proposedQuestionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    def student
    def course

    def setup() {
        student = new User("name", "username", 1, User.Role.STUDENT)
        userRepository.save(student)

        course = new Course("Course_name", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "AC12", "1SEM", Course.Type.TECNICO)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        courseExecutionRepository.save(courseExecution)
    }

    def "performance testing to create 10000 proposed questions"() {
        given: "a list for proposed questions"
        def list = new ArrayList<ProposedQuestionDto>()
        and: "10000 proposedQuestionDto"
        1.upto(1, {
            def propQuestionDto = new ProposedQuestionDto()
            propQuestionDto.setQuestion(createQuestionDto(it))
            propQuestionDto.setStudentId(student.getId())
            list.add(propQuestionDto)
        })

        when:
        1.upto(1, {
            proposedQuestionService.studentSubmitQuestion(course.getId(), list.get((it as int) - 1))
        })

        then:
        true
    }

    def createQuestionDto(key) {
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
        questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter))
        return questionDto
    }

    @TestConfiguration
    static class PropQuestionServiceImplTestContextConfiguration {

        @Bean
        ProposedQuestionService questionPropService() {
            return new ProposedQuestionService()
        }
    }
}

