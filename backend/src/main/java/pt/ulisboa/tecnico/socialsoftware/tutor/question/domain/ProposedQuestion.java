package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "proposed_questions")
public class ProposedQuestion {
    public enum Evaluation {
        APPROVED, REJECTED, AWAITING
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private String justification;

    @Enumerated(EnumType.STRING)
    private Evaluation evaluation = Evaluation.AWAITING;

    public ProposedQuestion() {}

    public ProposedQuestion(User student, Course course) {
        checkUserPermission(student, course, User.Role.STUDENT);
        this.student = student;
        student.addProposedQuestion(this);
        course.addProposedQuestion(this);
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id;}

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getStudent() { return student; }

    public void setStudent(User student) { this.student = student; }

    public User getTeacher() { return teacher; }

    public void setTeacher(User teacher) { this.teacher = teacher; }

    public void assignTeacher (User teacher, Course course) {
        checkUserPermission(teacher, course, User.Role.TEACHER);
        this.setTeacher(teacher);
    }

    public void evaluate (String justification, Evaluation evaluation){
        if (evaluation == Evaluation.REJECTED && justification == null) {
            throw new TutorException(ErrorMessage.JUSTIFICATION_IS_EMPTY);
        }
        if (evaluation == Evaluation.REJECTED && justification.trim().isEmpty()){
            throw new TutorException(ErrorMessage.JUSTIFICATION_IS_BLANK);
        }
        if (this.evaluation != Evaluation.AWAITING && this.teacher != null){
            throw new TutorException(ErrorMessage.PQ_ALREADY_EVALUATED);
        }

        setJustification(justification);
        setEvaluation(evaluation);
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Evaluation getEvaluation() { return evaluation; }

    public void setEvaluation(Evaluation evaluation) { this.evaluation = evaluation; }

    public void checkUserPermission(User user, Course course, User.Role role) {
        if (user.getRole() != role) {
            throw new TutorException(ErrorMessage.ACCESS_DENIED);
        }
        if (user.getCourseExecutions().stream()
                .noneMatch(courseExecution -> courseExecution.getCourse().getId().equals(course.getId()))) {
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED_COURSE);
        }
    }

    public void addQuestion(Question question, List<Topic> topics) {
        // If it has a topic, has to belong to the course's topics
        if (!topics.isEmpty() && !topics.stream()
                .allMatch(topic -> question.getCourse().getTopics().contains(topic))) {
            throw new TutorException(ErrorMessage.TOPIC_NOT_BELONGING_TO_COURSE);
        }
        topics.forEach(question::addTopic);
        this.question = question;
    }
}