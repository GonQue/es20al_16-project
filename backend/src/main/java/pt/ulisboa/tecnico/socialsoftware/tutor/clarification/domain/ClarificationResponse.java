package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationResponseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clarification_responses")
public class ClarificationResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "clarification_id")
    private ClarificationQuestion clarificationQuestion;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private String teacherResponse;

    @Column(name = "responseDate_date")
    private LocalDateTime responseDate = null;

    public ClarificationResponse() {
    }

    public ClarificationResponse(ClarificationQuestion c, User t, ClarificationResponseDto questionClarificationDto) {
        clarificationQuestion = c;
        teacher = t;
        teacherResponse = questionClarificationDto.getTeacherResponse();
        responseDate = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClarificationQuestion getClarificationQuestion() { return clarificationQuestion; }

    public void setClarificationQuestion(ClarificationQuestion clarificationQuestion) { this.clarificationQuestion = clarificationQuestion; }

    public User getTeacher() { return teacher; }

    public void setTeacher(User teacher) { this.teacher = teacher; }

    public String getTeacherResponse() {
        return teacherResponse;
    }

    public void setTeacherResponse(String teacherResponse) {
        this.teacherResponse = teacherResponse;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public void remove() {
        getTeacher().getClarificationResponses().remove(this);
        getClarificationQuestion().getResponses().remove(this);
        this.teacher = null;
        if(this.clarificationQuestion.getResponses().isEmpty()) {
            this.clarificationQuestion.setStatus(ClarificationQuestion.Status.NOT_ANSWERED);
            this.clarificationQuestion.setNeedClarification(true);
        }
        this.clarificationQuestion = null;
    }

    @Override
    public String toString() {
        return "ClarificationResponse{" +
                "id=" + id +
                ", clarification id=" + clarificationQuestion.getId() +
                ", teacher=" + teacher.getUsername() +
                ", response=" + teacherResponse +
                ", date=" + responseDate +
                '}';
    }
}
