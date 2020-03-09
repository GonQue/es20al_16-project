package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

class StudentSubmitQuestionTest extends Specification {
    static final String QUESTION_TITLE = "Question title"
    static final String QUESTION_CONTENT = "Question content"
    static final String OPTION_CONTENT = "Option content"
    static final String TOPIC_NAME = "Topic name"

    def questionPropService
    def student
    def studentDto
    def propQuestionDto
    def questionDto

    def setup() {
        questionPropService = new QuestionProposalService()

        student = new User("name", "username", 1, User.Role.STUDENT)
        student.setId(1)
        studentDto = new UserDto(student)

        questionDto = new QuestionDto()
        questionDto.setId(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
    }

    def 'the user is not a Student'() {
        given: "a user"
        def user = new User("name", "username2", 2, User.Role.TEACHER)
        user.setId(2)
        and: "a userDto"
        def userDto = new UserDto(user)
        and: "a proposedQuestionDto"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(userDto)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'the student is not enrolled in the course'() {
        given: "a course execution"
        def courseExecution = new CourseExecution()
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        and: "another student"
        def anotherStudent = new User("name", "username2", 2, User.Role.STUDENT)
        anotherStudent.setId(2)
        def anotherStudentDto = new UserDto(anotherStudent)
        and: "a proposedQuestionDto"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setId(1)
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(anotherStudentDto)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }

    def 'the topic for the new question exists in the student course'() {
        given: "a topic"
        def topic = new Topic()
        topic.setId(1)
        topic.setName(TOPIC_NAME)
        and: "a course"
        def course = new Course("course", Course.Type.TECNICO)
        course.addTopic(topic)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, "AC", "1S", Course.Type.TECNICO)
        def courseSet = new HashSet<CourseExecution>()
        courseSet.add(courseExecution)
        student.setCourseExecutions(courseSet)
        and: "a topicDto"
        def topicDto = new TopicDto(topic)
        def topics = new ArrayList<TopicDto>()
        topics.add(topicDto)
        and: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setId(1)
        questionDto.setTopics(topics)
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setStudent(studentDto)

        when:
        def result = questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        result.getKey() == 1
        result.getQuestion().getTopics().size() == 1
        result.getQuestion().getTopics().get(0).getId() == 1
        result.getQuestion().getTopics().get(0).getName() == TOPIC_NAME
        result.getStudent().getId() == student.getId()
        student.getCourseExecutions().size() == 1
        student.getCourseExecutions().get(0).getCourse().getTopics().size() == 1
        student.getCourseExecutions().get(0).getCourse().getTopics().getId() == 1
    }

    def 'the question is not empty'() {
        given: "a proposed question"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setId(1)
        propQuestionDto.setStudent(studentDto)

        when:
        def result = questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        result.getId() == 1
        result.getQuestion() != null
        result.getQuestion().getId() == 1
    }

    def 'the student is empty'() {
        given: "a proposed question"
        propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setQuestion(questionDto)
        propQuestionDto.setId(1)
        propQuestionDto.setStudent(null)

        when:
        questionPropService.studentSubmitQuestion(propQuestionDto)

        then:
        thrown(TutorException)
    }
}
