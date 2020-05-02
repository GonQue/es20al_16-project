package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clarifications")
public class ClarificationQuestion {
    public enum Status { ANSWERED, NOT_ANSWERED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private QuestionAnswer answer;

    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean needClarification;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = null;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clarificationQuestion", orphanRemoval=true)
    private List<ClarificationResponse> responses = new ArrayList<>();

    public ClarificationQuestion() {
    }

    public ClarificationQuestion(Question q, User s, QuestionAnswer a, ClarificationQuestionDto clarificationQuestionDto) {
        question = q;
        student = s;
        answer = a;
        setStatus(Status.NOT_ANSWERED);
        setNeedClarification(true);
        content = clarificationQuestionDto.getContent();
        creationDate = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

    public User getStudent() { return student; }

    public void setStudent(User student) { this.student = student; }

    public QuestionAnswer getAnswer() { return answer; }

    public void setAnswer(QuestionAnswer answer) { this.answer = answer; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getNeedClarification() {
        return needClarification;
    }

    public void setNeedClarification(Boolean b) {
        this.needClarification = b;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<ClarificationResponse> getResponses() { return responses; }

    public void addResponse(ClarificationResponse clarificationResponse) {
        responses.add(clarificationResponse);
    }

    public void remove() {
        getStudent().getClarification_questions().remove(this);
        getQuestion().getClarification_questions().remove(this);
        getResponses().forEach(response -> response.setClarificationQuestion(null));
        getResponses().clear();
        responses = null;
        question = null;
        student = null;
        answer = null;

    }

    public void askForAdditionalClarification() {
        this.needClarification = true;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                '}';
    }
}
