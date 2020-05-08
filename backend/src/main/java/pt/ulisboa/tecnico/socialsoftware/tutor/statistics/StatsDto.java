package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StatsDto implements Serializable {
    private Boolean publicDashboard = false;
    private Integer totalQuizzes = 0;
    private Integer totalAnswers = 0;
    private Integer totalUniqueQuestions = 0;
    private float correctAnswers = 0;
    private float improvedCorrectAnswers = 0;
    private Integer uniqueCorrectAnswers = 0;
    private Integer uniqueWrongAnswers = 0;
    private Integer totalAvailableQuestions = 0;
    private Integer totalClarificationQuestions = 0;
    private Integer totalPublicClarificationQuestions = 0;
    private Integer totalTournamentsCreated = 0;
    private Integer totalTournamentsJoined = 0;

    public Boolean getPublicDashboard() {
        return publicDashboard;
    }

    public void setPublicDashboard(Boolean publicDashboard) {
        this.publicDashboard = publicDashboard;
    }

    public void togglePublicDashboard(){
        this.publicDashboard = !this.publicDashboard;
    }

    public Integer getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(Integer totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Integer totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Integer getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(Integer totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }

    public float getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(float correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public float getImprovedCorrectAnswers() {
        return improvedCorrectAnswers;
    }

    public void setImprovedCorrectAnswers(float improvedCorrectAnswers) {
        this.improvedCorrectAnswers = improvedCorrectAnswers;
    }

    public Integer getUniqueCorrectAnswers() {
        return uniqueCorrectAnswers;
    }

    public void setUniqueCorrectAnswers(Integer uniqueCorrectAnswers) {
        this.uniqueCorrectAnswers = uniqueCorrectAnswers;
    }

    public Integer getUniqueWrongAnswers() {
        return uniqueWrongAnswers;
    }

    public void setUniqueWrongAnswers(Integer uniqueWrongAnswers) {
        this.uniqueWrongAnswers = uniqueWrongAnswers;
    }

    public Integer getTotalAvailableQuestions() {
        return totalAvailableQuestions;
    }

    public void setTotalAvailableQuestions(Integer totalAvailableQuestions) {
        this.totalAvailableQuestions = totalAvailableQuestions;
    }

    public Integer getTotalClarificationQuestions() {
        return totalClarificationQuestions;
    }

    public void setTotalClarificationQuestions(Integer totalClarificationQuestions) {
        this.totalClarificationQuestions = totalClarificationQuestions;
    }

    public Integer getTotalPublicClarificationQuestions() {
        return totalPublicClarificationQuestions;
    }

    public void setTotalPublicClarificationQuestions(Integer totalPublicClarificationQuestions) {
        this.totalPublicClarificationQuestions = totalPublicClarificationQuestions;
    }

    public Integer getTotalTournamentsCreated() {return totalTournamentsCreated; }

    public void setTotalTournamentsCreated(Integer totalTournamentsCreated) { this.totalTournamentsCreated = totalTournamentsCreated;    }

    public Integer getTotalTournamentsJoined() { return totalTournamentsJoined; }

    public void setTotalTournamentsJoined(Integer totalTournamentsJoined) { this.totalTournamentsJoined = totalTournamentsJoined; }

    @Override
    public String toString() {
        return "StatsDto{" +
                "publicDashboard=" + publicDashboard +
                ", totalQuizzes=" + totalQuizzes +
                ", totalAnswers=" + totalAnswers +
                ", totalUniqueQuestions=" + totalUniqueQuestions +
                ", correctAnswers=" + correctAnswers +
                ", improvedCorrectAnswers=" + improvedCorrectAnswers +
                ", uniqueCorrectAnswers=" + uniqueCorrectAnswers +
                ", uniqueWrongAnswers=" + uniqueWrongAnswers +
                ", totalClarificationQuestions=" + totalClarificationQuestions +
                ", totalPublicClarificationQuestions=" + totalPublicClarificationQuestions +
                ", totalTournamentsCreated" + totalTournamentsCreated +
                ", totalTournamentsJoined" + totalTournamentsJoined +
                '}';
    }
}
