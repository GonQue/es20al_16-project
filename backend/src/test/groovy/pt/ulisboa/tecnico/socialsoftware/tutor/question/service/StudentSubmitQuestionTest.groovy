package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionProposalService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class StudentSubmitQuestionTest extends Specification {
    static final String COURSE_ONE = "CourseOne"
    static final String QUESTION_TITLE = "Question title"
    static final String QUESTION_CONTENT = "Question content"
    static final String OPTION_CONTENT = "Option content"

    def questionPropService
    def course

    def setup() {
        questionPropService = new QuestionProposalService()
    }
    // verificar que aluno está inscrito

    def 'the user is not a Student'() {
        /*given: "a teacher"
        def user = new User()
        user.setKey(1)
        user.setRole(User.Role.TEACHER)

        when:
        questionPropService.studentSubmitQuestion()
        */
        expect: false
    }

    def 'the topic for the new question exists'() {
        // the proposed question is created
        expect: false
    }

    def 'create a question with no image and 2 options'() {
        given: "a student"
        def user = new User()
        user.setKey(1)
        user.setRole(User.Role.STUDENT)
        and: "a proposedQuestionDto"
        def propQuestionDto = new ProposedQuestionDto()
        propQuestionDto.setKey(1)
        propQuestionDto.setTitle(QUESTION_TITLE)
        propQuestionDto.setContent(QUESTION_CONTENT)
        and: "two options"
        def optionsDto = new OptionDto()
        optionsDto.setContent(OPTION_CONTENT)
        optionsDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionsDto)
        optionsDto = new OptionDto()
        optionsDto.setContent(OPTION_CONTENT)
        optionsDto.setCorrect(true)
        options.add(optionsDto)
        expect: false
    }

    def 'create a question with no options'() {
        // an exception is thrown
        expect: false
    }

    def 'question title is empty'() {
        // an exception is thrown
        expect: false
    }

    def 'question title is blank'() {
        // an exception is thrown
        expect: false
    }

    def 'question content is empty'() {
        // an exception is thrown
        expect: false
    }

    def 'question content is blank'() {
        // an exception is thrown
        expect: false
    }
}
