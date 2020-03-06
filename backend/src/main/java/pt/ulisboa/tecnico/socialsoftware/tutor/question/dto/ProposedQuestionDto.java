package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;

public class ProposedQuestionDto extends QuestionDto {
    private Integer userKey;
    private String justification;
    private String evaluation;
    private Integer assignedTeacherID; // maybe its needed

    public ProposedQuestionDto() {
    }

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {
        super(proposedQuestion);
    }

    public void setUserKey(Integer key) { userKey = key; }
    public Integer getUserKey() { return userKey; }

    public void setJustification(String j) { justification = j; }
    public String getJustification() { return justification; }

    public void setEvaluation(String eval) {evaluation = eval; }
    public String getEvaluation() { return evaluation; }

    public void setAssignedTeacherID(Integer id) { assignedTeacherID = id; }
    public Integer getAssignedTeacherID() { return assignedTeacherID; }
}
