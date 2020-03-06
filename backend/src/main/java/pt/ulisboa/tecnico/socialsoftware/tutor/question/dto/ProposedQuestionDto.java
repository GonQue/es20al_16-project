package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ProposedQuestion;

public class ProposedQuestionDto extends QuestionDto {
    private Integer userID;
    private String justification;
    private String evaluation;
    private Integer assignedTeacherID; // maybe its needed

    public ProposedQuestionDto() {
    }

    public ProposedQuestionDto(ProposedQuestion proposedQuestion) {

    }

    public void setUserKey(Integer id) { userID = id; }
    public Integer getUserKey(Integer id) { return userID; }

    public void setJustification(String j) { justification = j; }
    public String getJustification() { return justification; }

    public void setEvaluation(String eval) {evaluation = eval; }
    public String getEvaluation() { return evaluation; }

    public void setAssignedTeacherID(Integer id) { assignedTeacherID = id; }
    public Integer getAssignedTeacherID() { return assignedTeacherID; }
}
