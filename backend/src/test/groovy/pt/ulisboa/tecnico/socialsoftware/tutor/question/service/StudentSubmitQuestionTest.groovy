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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.PROPQUESTION_MISSING_QUESTION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_EMPTY


@DataJpaTest
class StudentSubmitQuestionTest extends Specification {
    static final String QUESTION_TITLE = "Question title"
    static final String QUESTION_CONTENT = "Question content"
    static final String OPTION_CONTENT = "Option content"
    static final String TOPIC_NAME = "Topic name"
    static final String COURSE_NAME = "Course name"
    static final String COURSE_ACRONYM = "AC"
    static final String COURSE_ACADEMIC_TERM = "2S"

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
    TopicRepository topicRepository

    def course
    def propQuestionDto
    def student
    def questionDto

    def setup() {
        student = new User("name", "username", 1, User.Role.STUDENT)
        userRepository.save(student)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter))
    }

    def 'the user is not a Student'() {
        given: "a user"
        def user = new User("name", "username2", 2, User.Role.TEACHER)
        userRepository.save(user)
        and: "a proposedQuestionDto"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(new UserDto(user))

        when:
        proposedQuestionService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ACCESS_DENIED
    }

    def 'the student is not enrolled in the course'() {
        given: "a course execution"
        def courseExecution = new CourseExecution(course, COURSE_ACRONYM, COURSE_ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        and: "another student"
        def anotherStudent = new User("name", "username2", 2, User.Role.STUDENT)
        userRepository.save(anotherStudent)
        and: "a proposedQuestionDto"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(new UserDto(anotherStudent))

        when:
        proposedQuestionService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_ENROLLED_COURSE
    }

    def 'the topic for the new question exists in the student course'() {
        given: "a topic"
        def topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)
        topic.setCourse(course)
        course.addTopic(topic)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, "AC", "1S", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        def courseSet = new HashSet<CourseExecution>()
        courseSet.add(courseExecution)
        student.setCourseExecutions(courseSet)
        and: "a topicDto"
        def topicDto = new TopicDto(topic)
        def topics = new ArrayList<TopicDto>()
        topics.add(topicDto)
        and: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        questionDto.setTopics(topics)
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(new UserDto(student))

        when:
        def result = proposedQuestionService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        result.getQuestion().getTopics().size() == 1
        result.getQuestion().getTopics().get(0).getId() == topic.getId()
        result.getQuestion().getTopics().get(0).getName() == TOPIC_NAME
    }

    def 'student submits a question'() {
        given: "a course execution"
        def courseExecution = new CourseExecution(course, COURSE_ACRONYM, COURSE_ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        courseExecutionRepository.save(courseExecution)
        and: "a proposed question"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(new UserDto(student))

        when:
        def result = proposedQuestionService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        result.getQuestion() != null
        result.getQuestion().getTitle() == QUESTION_TITLE
        result.getQuestion().getContent() == QUESTION_CONTENT
        result.getStudent().getId() == student.getId()
    }

    @Unroll("invalid arguments: #hasQuestion | #hasStudent || errorMessage")
    def "invalid input values"() {
        given: "a proposed questionDto"
        propQuestionDto = new ProposedQuestionDto()
        addQuestion(hasQuestion)
        addStudent(hasStudent)
        when:
        proposedQuestionService.studentSubmitQuestion(course.getId(), propQuestionDto)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == errorMessage

        where:
        hasQuestion  | hasStudent  || errorMessage
        false        | true        || PROPQUESTION_MISSING_QUESTION
        true         | false       || USER_IS_EMPTY
    }

    def addQuestion(Boolean hasQuestion) {
        def question = hasQuestion ? questionDto : null
        propQuestionDto.setQuestion(question)
    }

    def addStudent(Boolean hasStudent) {
        if (hasStudent) {
            def courseExecution = new CourseExecution(course, COURSE_ACRONYM, COURSE_ACADEMIC_TERM, Course.Type.TECNICO)
            courseExecution.addUser(student)
            student.addCourse(courseExecution)
            propQuestionDto.setStudent(new UserDto(student))
        }
        else {
            propQuestionDto.setStudent(null)
        }
    }

    @TestConfiguration
    static class PropQuestionServiceImplTestContextConfiguration {

        @Bean
        ProposedQuestionService questionPropService() {
            return new ProposedQuestionService()
        }
    }
}
