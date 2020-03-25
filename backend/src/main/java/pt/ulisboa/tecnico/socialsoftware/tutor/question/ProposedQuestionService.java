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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@Service
public class ProposedQuestionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ProposedQuestionRepository pqRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    private User getStudent(ProposedQuestionDto proposedQuestionDto) {
        Integer studentId = proposedQuestionDto.getStudentId();
        if (studentId == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }
        return userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));
    }

    private Question createQuestion(Course course, ProposedQuestionDto proposedQuestionDto) {
        if (proposedQuestionDto.getQuestion() == null) {
            throw new TutorException(ErrorMessage.PROPQUESTION_MISSING_QUESTION);
        }
        Question question = new Question(course, proposedQuestionDto.getQuestion());
        question.setStatus(Question.Status.SUBMITTED);
        addTopicsToQuestion(proposedQuestionDto, question);
        question.setCreationDate(LocalDateTime.now());
        questionRepository.save(question);
        return question;
    }

    private void addTopicsToQuestion(ProposedQuestionDto proposedQuestionDto, Question question) {
        if (proposedQuestionDto.getQuestion().getTopics() != null) {
            for (TopicDto topicDto: proposedQuestionDto.getQuestion().getTopics()) {
                Topic topic = topicRepository.findTopicByName(question.getCourse().getId(), topicDto.getName());
                question.addTopic(topic);
            }
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto studentSubmitQuestion(int courseId, ProposedQuestionDto proposedQuestionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));

        User student = getStudent(proposedQuestionDto);
        ProposedQuestion proposedQuestion = new ProposedQuestion(student, course);

        Question question = createQuestion(course, proposedQuestionDto);
        proposedQuestion.addQuestion(question);

        pqRepository.save(proposedQuestion);
        return new ProposedQuestionDto(proposedQuestion);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto teacherEvaluatesProposedQuestion(int courseId, ProposedQuestionDto pqDto) {

        Course course = findCourse(courseId);
        User teacher = findTeacher(pqDto);
        ProposedQuestion pq = findProposedQuestion(pqDto);

        String justification = pqDto.getJustification();
        String evaluation = pqDto.getEvaluation();
        pq.evaluate(justification, ProposedQuestion.Evaluation.valueOf(evaluation));

        pq.assignTeacher(teacher, course);

        return new ProposedQuestionDto(pq);
    }

    private ProposedQuestion findProposedQuestion(ProposedQuestionDto pqDto) {
        return pqRepository.findById(pqDto.getId()).orElseThrow(() -> new TutorException(ErrorMessage.PQ_NOT_FOUND));
    }

    private User findTeacher(ProposedQuestionDto pqDto) {
        if (pqDto.getTeacher() == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }
        int teacherId = pqDto.getTeacher().getId();
        return userRepository.findById(teacherId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, teacherId));
    }

    private Course findCourse(int courseId) {
        return courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));
    }

}
