package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class QuestionProposalService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto studentSubmitQuestion(int courseId, ProposedQuestionDto proposedQuestionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));

        if (proposedQuestionDto.getStudent() == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }

        int studentId = proposedQuestionDto.getStudent().getId();
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        ProposedQuestion proposedQuestion = new ProposedQuestion(student, course, proposedQuestionDto);

        QuestionDto questionDto = questionService.createQuestion(courseId, proposedQuestionDto.getQuestion());
        Question question = questionRepository.findByKey(questionDto.getKey()).orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));

        proposedQuestion.setQuestion(question);
        this.entityManager.persist(proposedQuestion);

        return new ProposedQuestionDto(proposedQuestion);

        // student in not empty
        // check if is a student
        // check if student is enrolled in the course
        // check if the topic is from the students course
        // question is empty


        /*if (proposedQuestionDto.getKey() == null) {
            int maxNumber = propQuestionRepository.getMaxPropQuestionNumber() != null ?
                    propQuestionRepository.getMaxPropQuestionNumber() : 0;
            proposedQuestionDto.setKey(maxNumber + 1);
        }*/
        //save it in the database*/
    }
}
