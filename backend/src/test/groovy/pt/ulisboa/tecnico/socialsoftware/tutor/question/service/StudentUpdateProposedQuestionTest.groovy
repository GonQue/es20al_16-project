package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

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
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class StudentUpdateProposedQuestionTest extends Specification {
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CONTENT = "Option Content"
    public static final String JUSTIFICATION = "Justification"
    public static final String NEW_QUESTION_TITLE = "New Question Title"
    public static final String NEW_QUESTION_CONTENT = "New Question Content"

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

    def proposedQuestion

    def setup() {
        def teacher = new User("teacher", "teacher", 1, User.Role.TEACHER)
        userRepository.save(teacher)

        def student = new User("student", "student", 2, User.Role.STUDENT)
        userRepository.save(student)

        def course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, "ES", "2S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        course.addCourseExecution(courseExecution)

        Set<CourseExecution> courseExecSet = new HashSet<>()
        courseExecSet.add(courseExecution)
        teacher.setCourseExecutions(courseExecSet)

        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        def question = new Question(course, questionDto)
        questionRepository.save(question)

        proposedQuestion = new ProposedQuestion()
        proposedQuestion.setStudent(student)
        proposedQuestion.setQuestion(question)
        proposedQuestion.setTeacher(teacher)
        proposedQuestionRepository.save(proposedQuestion)
    }

    @Unroll("the student updates a #evaluation proposed question with #justification")
    def 'valid update cases'() {
        given: "a proposed question"
        proposedQuestion.setEvaluation(evaluation)
        proposedQuestion.setJustification(justification)

        and: "an updated proposed question Dto"
        def proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        proposedQuestionDto.getQuestion().setTitle(NEW_QUESTION_TITLE)
        proposedQuestionDto.getQuestion().setContent(NEW_QUESTION_CONTENT)

        when:
        proposedQuestionService.updateProposedQuestion(proposedQuestion.getId(), proposedQuestionDto)

        then: "the proposed question is updated"
        proposedQuestionRepository.count() == 1L
        def result = proposedQuestionRepository.findAll().get(0)
        result.getId() == proposedQuestion.getId()
        result.getQuestion().getTitle() == NEW_QUESTION_TITLE
        result.getQuestion().getContent() == NEW_QUESTION_CONTENT
        result.getJustification() == justification
        result.getEvaluation() == ProposedQuestion.Evaluation.AWAITING

        where:
        evaluation                              | justification
        ProposedQuestion.Evaluation.REJECTED    | JUSTIFICATION
        ProposedQuestion.Evaluation.AWAITING    | null
    }

    @Unroll("the student updates a #evaluation proposed question | errorMessage")
    def 'invalid update cases'() {
        given: "a proposed question"
        proposedQuestion.setEvaluation(evaluation)

        and: "an updated proposed question Dto"
        def proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        proposedQuestionDto.getQuestion().setTitle(NEW_QUESTION_TITLE)
        proposedQuestionDto.getQuestion().setContent(NEW_QUESTION_CONTENT)

        when:
        proposedQuestionService.updateProposedQuestion(proposedQuestion.getId(), proposedQuestionDto)

        then: "the proposed question is updated"
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        evaluation                              || errorMessage
        ProposedQuestion.Evaluation.APPROVED    || PROPQUESTION_CANT_BE_UPDATED
        ProposedQuestion.Evaluation.AVAILABLE   || PROPQUESTION_CANT_BE_UPDATED
    }

    def 'updated content is null'() {
        given: "an updated proposed question with null content"
        def proposedQuestionDto = new ProposedQuestionDto(proposedQuestion)
        proposedQuestionDto.getQuestion().setContent(null)

        when:
        proposedQuestionService.updateProposedQuestion(proposedQuestion.getId(), proposedQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_CONTENT_FOR_QUESTION
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