package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class ProposedQuestionDto {
    private Integer id;
    private Integer studentId;
    private QuestionDto question;
    private UserDto teacher;
    private String justification;
    private String evaluation;

    public ProposedQuestionDto() {}

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {
        this.id = proposedQuestion.getId();
        this.question = new QuestionDto(proposedQuestion.getQuestion());
        this.studentId = proposedQuestion.getStudent().getId();
        this.evaluation = proposedQuestion.getEvaluation().name();

        if (proposedQuestion.getTeacher() != null) {
            evaluate(proposedQuestion);
        }
    }

    private void evaluate(ProposedQuestion proposedQuestion) {
        this.teacher = new UserDto(proposedQuestion.getTeacher());
        this.justification = proposedQuestion.getJustification();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }

    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public UserDto getTeacher() { return teacher; }

    public void setTeacher(UserDto teacher) { this.teacher = teacher; }

    public String getJustification() { return justification; }

    public void setJustification(String justification) { this.justification = justification; }

    public String getEvaluation() { return evaluation; }

    public void setEvaluation(String evaluation) { this.evaluation = evaluation; }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }
}
