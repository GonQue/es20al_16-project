package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;

public class ProposedQuestionDto {

    private Integer id;
    private QuestionDto question;
    private Integer studentId;

    public ProposedQuestionDto() {}

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {
        this.id = proposedQuestion.getId();
        this.question = new QuestionDto(proposedQuestion.getQuestion());
        this.studentId = proposedQuestion.getStudent().getId();
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public Integer getStudentId() { return studentId; }

    public void setStudentId(Integer studentId) { this.studentId = studentId; }
}
