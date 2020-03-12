package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clarifications")
public class QuestionClarification {
    public enum Status { ANSWERED, NOT_ANSWERED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = null;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clarificationQuestion", orphanRemoval=true)
    private List<ClarificationResponse> responses = new ArrayList<>();

    public QuestionClarification() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question) { this.question = question; }

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

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                '}';
    }
}
