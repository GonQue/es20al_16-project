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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class RemoveProposedQuestionTest extends Specification {

    @Autowired
    QuestionService questionService

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

    @Autowired
    ProposedQuestionRepository proposedQuestionRepository

    def proposedQuestion

    def setup() {
        def student = new User("name", "username", 1, User.Role.STUDENT)
        userRepository.save(student)

        def course = new Course("Course name", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "AC", "2S", Course.Type.TECNICO)

        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        courseExecutionRepository.save(courseExecution)

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
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

        proposedQuestion = new ProposedQuestion()
        proposedQuestion.setQuestion(question)
        proposedQuestion.setStudent(student)
        proposedQuestionRepository.save(proposedQuestion)
    }

    def "remove a proposed question"() {
        when:
        proposedQuestionService.deleteProposedQuestion(proposedQuestion.getId())

        then: "the proposed question is removed"
        proposedQuestionRepository.count() == 0L
    }

    @TestConfiguration
    static class PropQuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        ProposedQuestionService questionPropService() {
            return new ProposedQuestionService()
        }
    }
}
