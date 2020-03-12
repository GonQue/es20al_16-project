package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "proposed_questions")
public class ProposedQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // EAGER -> when it reads a PQ, loads a question too
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User student;

    public ProposedQuestion() {
    }

    public ProposedQuestion(User student, Course course, ProposedQuestionDto proposedQuestionDto) {
        checkStudent(student, course);
        if (proposedQuestionDto.getQuestion() == null) {
            throw new TutorException(ErrorMessage.PROPQUESTION_MISSING_QUESTION);
        }
       /*if (!proposedQuestionDto.getQuestion().getTopics().isEmpty() &&
                !proposedQuestionDto.getQuestion().getTopics().stream()
                .allMatch(topic -> topic.getCourseId().equals(question.getCourse().getId()))) {
            throw new TutorException(ErrorMessage.TOPIC_NOT_BELONGING_TO_COURSE);
        }*/
        this.student = student;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id;}

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getStudent() { return student; }

    public void setStudent(User student) { this.student = student; }

    public void checkStudent(User student, Course course) {
        if (student.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.ACCESS_DENIED);
        }
        if (student.getCourseExecutions().stream()
                .noneMatch(courseExecution -> courseExecution.getCourse().getId().equals(course.getId()))) {
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED_COURSE);
        }
    }
}
