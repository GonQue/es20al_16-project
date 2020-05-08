package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;

public class StudentDto implements Serializable {
    private String username;
    private String name;
    private Boolean publicDashboard;
    private Integer numberOfTeacherQuizzes;
    private Integer numberOfInClassQuizzes;
    private Integer numberOfStudentQuizzes;
    private Integer numberOfAnswers;
    private Integer numberOfTeacherAnswers;
    private Integer numberOfInClassAnswers;
    private Integer numberOfStudentAnswers;
    private Integer numberOfClarificationQuestions;
    private Integer numberOfPublicClarificationQuestions;
    private Integer numberOfProposedQuestions;
    private Integer numberOfApprovedProposedQuestions;
    private int percentageOfCorrectAnswers = 0;
    private int percentageOfCorrectTeacherAnswers = 0;
    private int percentageOfCorrectInClassAnswers = 0;
    private int percentageOfCorrectStudentAnswers = 0;
    private String creationDate;
    private String lastAccess;

    public StudentDto(User user) {
        this.username = user.getUsername();
        this.name = user.getName();
        this.publicDashboard = user.getPublicDashboard();
        this.numberOfTeacherQuizzes = user.getNumberOfTeacherQuizzes();
        this.numberOfInClassQuizzes = user.getNumberOfInClassQuizzes();
        this.numberOfStudentQuizzes = user.getNumberOfStudentQuizzes();
        this.numberOfAnswers = user.getNumberOfTeacherAnswers() + user.getNumberOfInClassAnswers() + user.getNumberOfStudentAnswers();
        this.numberOfTeacherAnswers = user.getNumberOfTeacherAnswers();
        this.numberOfInClassAnswers = user.getNumberOfInClassAnswers();
        this.numberOfStudentAnswers = user.getNumberOfStudentAnswers();
        this.numberOfClarificationQuestions = user.getNumberOfClarificationQuestions();
        this.numberOfPublicClarificationQuestions = user.getNumberOfPublicClarificationQuestions();
        this.numberOfProposedQuestions = user.getNumberOfProposedQuestions();
        this.numberOfApprovedProposedQuestions = user.getNumberOfApprovedProposedQuestions();
        this.lastAccess = DateHandler.toISOString(user.getLastAccess());
        this.creationDate = DateHandler.toISOString(user.getCreationDate());

        if (this.numberOfTeacherAnswers != 0)
            this.percentageOfCorrectTeacherAnswers = user.getNumberOfCorrectTeacherAnswers() * 100 / this.numberOfTeacherAnswers;
        if (this.numberOfInClassAnswers != 0)
            this.percentageOfCorrectInClassAnswers = user.getNumberOfCorrectInClassAnswers() * 100 / this.numberOfInClassAnswers;
        if (this.numberOfStudentAnswers != 0)
            this.percentageOfCorrectStudentAnswers = user.getNumberOfCorrectStudentAnswers() * 100 / this.numberOfStudentAnswers;
        if (this.numberOfAnswers != 0)
            this.percentageOfCorrectAnswers = (user.getNumberOfCorrectTeacherAnswers() + user.getNumberOfCorrectInClassAnswers() + user.getNumberOfCorrectStudentAnswers())  * 100 / this.numberOfAnswers;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublicDashboard() {
        return publicDashboard;
    }

    public void setPublicDashboard(Boolean publicDashboard) {
        this.publicDashboard = publicDashboard;
    }

    public void togglePublicDashboard(){
        this.publicDashboard = !this.publicDashboard;
    }

    public Integer getNumberOfTeacherQuizzes() {
        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(Integer numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public Integer getNumberOfStudentQuizzes() {
        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(Integer numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getNumberOfTeacherAnswers() {
        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(Integer numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public int getPercentageOfCorrectAnswers() {
        return percentageOfCorrectAnswers;
    }

    public void setPercentageOfCorrectAnswers(int percentageOfCorrectAnswers) {
        this.percentageOfCorrectAnswers = percentageOfCorrectAnswers;
    }

    public int getPercentageOfCorrectTeacherAnswers() {
        return percentageOfCorrectTeacherAnswers;
    }

    public void setPercentageOfCorrectTeacherAnswers(int percentageOfCorrectTeacherAnswers) {
        this.percentageOfCorrectTeacherAnswers = percentageOfCorrectTeacherAnswers;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }

    public Integer getNumberOfInClassQuizzes() {
        return numberOfInClassQuizzes;
    }

    public void setNumberOfInClassQuizzes(Integer numberOfInClassQuizzes) {
        this.numberOfInClassQuizzes = numberOfInClassQuizzes;
    }

    public Integer getNumberOfInClassAnswers() {
        return numberOfInClassAnswers;
    }

    public void setNumberOfInClassAnswers(Integer numberOfInClassAnswers) {
        this.numberOfInClassAnswers = numberOfInClassAnswers;
    }

    public Integer getNumberOfStudentAnswers() {
        return numberOfStudentAnswers;
    }

    public void setNumberOfStudentAnswers(Integer numberOfStudentAnswers) {
        this.numberOfStudentAnswers = numberOfStudentAnswers;
    }

    public Integer getNumberOfClarificationQuestions() { return numberOfClarificationQuestions; }

    public void setNumberOfClarificationQuestions(Integer numberOfClarificationQuestions) {
        this.numberOfClarificationQuestions = numberOfClarificationQuestions;
    }

    public Integer getNumberOfPublicClarificationQuestions() {
        return numberOfPublicClarificationQuestions;
    }

    public void setNumberOfPublicClarificationQuestions(Integer numberOfPublicClarificationQuestions) {
        this.numberOfPublicClarificationQuestions = numberOfPublicClarificationQuestions;
    }

    public Integer getNumberOfProposedQuestions() { return numberOfProposedQuestions; }

    public void setNumberOfProposedQuestions(Integer numberOfProposedQuestions) {
        this.numberOfProposedQuestions = numberOfProposedQuestions;
    }

    public Integer getNumberOfApprovedProposedQuestions() { return numberOfApprovedProposedQuestions; }

    public void setNumberOfApprovedProposedQuestions(Integer numberOfApprovedProposedQuestions) {
        this.numberOfApprovedProposedQuestions = numberOfApprovedProposedQuestions;
    }

    public int getPercentageOfCorrectInClassAnswers() {
        return percentageOfCorrectInClassAnswers;
    }

    public void setPercentageOfCorrectInClassAnswers(int percentageOfCorrectInClassAnswers) {
        this.percentageOfCorrectInClassAnswers = percentageOfCorrectInClassAnswers;
    }

    public int getPercentageOfCorrectStudentAnswers() {
        return percentageOfCorrectStudentAnswers;
    }

    public void setPercentageOfCorrectStudentAnswers(int percentageOfCorrectStudentAnswers) {
        this.percentageOfCorrectStudentAnswers = percentageOfCorrectStudentAnswers;
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", publicDashboard='" + publicDashboard + '\'' +
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfAnswers=" + numberOfAnswers +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", percentageOfCorrectAnswers=" + percentageOfCorrectAnswers +
                ", percentageOfCorrectTeacherAnswers=" + percentageOfCorrectTeacherAnswers +
                ", numberOfClarificationQuestions=" + numberOfClarificationQuestions +
                ", numberOfPublicClarificationQuestions=" + numberOfPublicClarificationQuestions +
                ", creationDate='" + creationDate + '\'' +
                ", lastAccess='" + lastAccess + '\'' +
                '}';
    }
}
