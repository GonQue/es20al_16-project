package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

public class QuestionProposalService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProposedQuestionRepository pqRepository;


    public ProposedQuestionDto studentSubmitQuestion(ProposedQuestionDto proposedQuestionDto) {
        return null;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto teacherEvaluatesSubmittedQuestion(int courseId, ProposedQuestionDto pqDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));

        if (pqDto.getTeacher() == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }
        Integer teacherId = pqDto.getTeacher().getId();
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, teacherId));

        ProposedQuestion pq = pqRepository.findById(pqDto.getId()).orElseThrow(() -> new TutorException(ErrorMessage.PQ_NOT_FOUND));

        String justification = pqDto.getJustification();
        String evaluation = pqDto.getEvaluation();
        pq.evaluate(justification, ProposedQuestion.Evaluation.valueOf(evaluation));

        pq.assignTeacher(teacher, course);

        return new ProposedQuestionDto(pq);
    }

}
