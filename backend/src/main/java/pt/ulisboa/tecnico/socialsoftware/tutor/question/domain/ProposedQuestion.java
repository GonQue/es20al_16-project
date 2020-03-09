package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ProposedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public class ProposedQuestion extends Question {

    public enum Evaluation {
        APPROVED, REJECTED, AWAITING
    }

    private User student;
    private User teacher;
    private String justification;
    private Evaluation evaluation = Evaluation.AWAITING;

    public ProposedQuestion(Course course, ProposedQuestionDto proposedQuestionDto) {
        super(course, proposedQuestionDto);
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }
}
