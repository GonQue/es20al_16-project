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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ProposedQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.util.Comparator;
import java.util.ArrayList;
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
        List<Topic> topics = getTopics(courseId, proposedQuestionDto);

        ProposedQuestion proposedQuestion = new ProposedQuestion(student, course);
        Question question = createQuestion(course, proposedQuestionDto);
        proposedQuestion.addQuestion(question, topics);
        pqRepository.save(proposedQuestion);
        return new ProposedQuestionDto(proposedQuestion);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto teacherEvaluatesProposedQuestion(ProposedQuestionDto pqDto) {
        Course course = getCourse(pqDto.getId());
        User teacher = getTeacher(pqDto);
        ProposedQuestion pq = findProposedQuestion(pqDto.getId());

        String justification = pqDto.getJustification();
        String evaluation = pqDto.getEvaluation();
        pq.evaluate(justification, ProposedQuestion.Evaluation.valueOf(evaluation));

        pq.assignTeacher(teacher, course);

        return new ProposedQuestionDto(pq);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ProposedQuestionDto> getStudentProposedQuestions(int id) {
        User student = userRepository.findById(id).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));
        return student.getProposedQuestions().stream().map(ProposedQuestionDto::new).
                sorted(Comparator.comparing(ProposedQuestionDto::getId).reversed()).
                collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ProposedQuestionDto> getCourseProposedQuestions(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));
        return course.getProposedQuestions().stream().map(ProposedQuestionDto::new).
                sorted(Comparator.comparing(ProposedQuestionDto::getId).reversed()).
                collect(Collectors.toList());
    }

    private User getStudent(ProposedQuestionDto proposedQuestionDto) {
        UserDto student = proposedQuestionDto.getStudent();
        if (student == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }
        return userRepository.findByUsername(student.getUsername());
    }

    private Question createQuestion(Course course, ProposedQuestionDto proposedQuestionDto) {
        Question question = new Question(course, proposedQuestionDto.getQuestion());
        question.setStatus(Question.Status.SUBMITTED);
        questionRepository.save(question);
        return question;
    }

    private List<Topic> getTopics(int courseId, ProposedQuestionDto proposedQuestionDto) {
        List<Topic> topics = new ArrayList<>();
        if (proposedQuestionDto.getQuestion() == null) {
            throw new TutorException(ErrorMessage.PROPQUESTION_MISSING_QUESTION);
        }
        if (proposedQuestionDto.getQuestion().getTopics() != null) {
            for (TopicDto topicDto: proposedQuestionDto.getQuestion().getTopics()) {
                Topic topic = topicRepository.findTopicByName(courseId, topicDto.getName());
                topics.add(topic);
            }
        }
        return topics;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestion findProposedQuestion(int pqId) {
        return pqRepository.findById(pqId).orElseThrow(() -> new TutorException(ErrorMessage.PQ_NOT_FOUND));
    }

    private User getTeacher(ProposedQuestionDto pqDto) {
        if (pqDto.getTeacher() == null) {
            throw new TutorException(ErrorMessage.USER_IS_EMPTY);
        }
        return userRepository.findByUsername(pqDto.getTeacher().getUsername());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Course getCourse(int pqId) {
        ProposedQuestion proposedQuestion = pqRepository.findById(pqId).orElseThrow(() -> new TutorException(ErrorMessage.PQ_NOT_FOUND));
        return proposedQuestion.getQuestion().getCourse();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteProposedQuestion(int proposedQuestionId) {
        ProposedQuestion proposedQuestion = findProposedQuestion(proposedQuestionId);

        if (proposedQuestion.canBeRemovedOrUpdated()) {
            Course course = getCourse(proposedQuestionId);
            course.removeProposedQuestion(proposedQuestion);
            pqRepository.delete(proposedQuestion);
            questionService.deleteQuestion(proposedQuestion.getQuestion());
        }
        else
            throw new TutorException(ErrorMessage.PROPQUESTION_CANT_BE_REMOVED);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto turnAvailable(int pqId, ProposedQuestionDto pqDto) {
        ProposedQuestion pq = findProposedQuestion(pqId);
        pq.evaluate("", ProposedQuestion.Evaluation.AVAILABLE);

        QuestionDto questionDto = pqDto.getQuestion();
        int questionId = pq.getQuestion().getId();
        questionService.questionSetStatus(questionId, Question.Status.AVAILABLE);
        questionService.updateQuestion(questionId, questionDto);

        Course course = getCourse(pqId);
        course.removeProposedQuestion(pq);

        return new ProposedQuestionDto(pq);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ProposedQuestionDto updateProposedQuestion(Integer propQuestionId, ProposedQuestionDto proposedQuestionDto) {
        ProposedQuestion proposedQuestion = findProposedQuestion(propQuestionId);
        if (proposedQuestion.canBeRemovedOrUpdated()) {
            questionService.updateQuestion(proposedQuestion.getQuestion().getId(), proposedQuestionDto.getQuestion());
            proposedQuestion.setEvaluation(ProposedQuestion.Evaluation.AWAITING);
            return new ProposedQuestionDto(proposedQuestion);
        }
        else
            throw new TutorException(ErrorMessage.PROPQUESTION_CANT_BE_UPDATED);
    }
}