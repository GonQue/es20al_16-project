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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private QuestionService questionService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto studentSubmitQuestion(int courseId, ProposedQuestionDto proposedQuestionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));

        User student = getStudent(proposedQuestionDto);
        ProposedQuestion proposedQuestion = new ProposedQuestion(student, course);

        Question question = createQuestion(courseId, proposedQuestionDto);
        proposedQuestion.addQuestion(question);

        pqRepository.save(proposedQuestion);
        return new ProposedQuestionDto(proposedQuestion);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto teacherEvaluatesProposedQuestion(ProposedQuestionDto pqDto) {
        Course course = getCourse(pqDto.getId());
        User teacher = getTeacher(pqDto);
        ProposedQuestion pq = findProposedQuestion(pqDto);

        String justification = pqDto.getJustification();
        String evaluation = pqDto.getEvaluation();
        pq.evaluate(justification, ProposedQuestion.Evaluation.valueOf(evaluation));

        pq.assignTeacher(teacher, course);

        return new ProposedQuestionDto(pq);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ProposedQuestionDto> getProposedQuestions(int id) {
        User student = findStudentById(id);
        return student.getProposedQuestions().stream().map(ProposedQuestionDto::new).
                sorted(Comparator.comparing(ProposedQuestionDto::getId)).
                collect(Collectors.toList());
    }

    private User getStudent(ProposedQuestionDto proposedQuestionDto) {
        Integer studentId = proposedQuestionDto.getStudentId();
        if (studentId == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }
        return userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));
    }

    private Question createQuestion(int courseId, ProposedQuestionDto proposedQuestionDto) {
        if (proposedQuestionDto.getQuestion() == null) {
            throw new TutorException(ErrorMessage.PROPQUESTION_MISSING_QUESTION);
        }
        QuestionDto questionDto = questionService.createQuestion(courseId, proposedQuestionDto.getQuestion());
        Question question = questionRepository.findByKey(questionDto.getKey()).orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));
        addTopicsToQuestion(proposedQuestionDto, question);
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

    private ProposedQuestion findProposedQuestion(ProposedQuestionDto pqDto) {
        return pqRepository.findById(pqDto.getId()).orElseThrow(() -> new TutorException(ErrorMessage.PQ_NOT_FOUND));
    }

    private User getTeacher(ProposedQuestionDto pqDto) {
        if (pqDto.getTeacherId() == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }
        int teacherId = pqDto.getTeacherId();
        return userRepository.findById(teacherId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, teacherId));
    }

    private User findStudentById(int id){
        return userRepository.findById(id).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));
    }

    public Course getCourse(int pqId) {
        ProposedQuestion proposedQuestion = pqRepository.findById(pqId).orElseThrow(() -> new TutorException(ErrorMessage.PQ_NOT_FOUND));
        return proposedQuestion.getQuestion().getCourse();
    }

}
