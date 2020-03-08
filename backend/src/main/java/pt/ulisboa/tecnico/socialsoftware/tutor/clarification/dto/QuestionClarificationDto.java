package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import java.io.Serializable;

public class QuestionClarificationDto implements Serializable {

    /*private Integer id;
    private Integer questionId;
    private Integer userId;
    private String content;
    private String status;
    private String creationDate = null;
    private String answerDate = null
    private Integer teacherId;
    private String teacherResponse;

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public QuestionClarificationDto() {
    }


    public QuestionClarificationDto(QuestionClarification clarification) {
        this.id = clarification.getId();
        this.key = clarification.getKey();
        this.questionId = clarification.getQuestionId();
        this.content = clarification.getContent();
        this.status = clarification.getStatus().name();
        this.numberOfAnswers = clarification.getNumberOfAnswers();
        this.teacherId = clarification.getTeacherId();
        this.teacherResponse = clarification.getTeacherResponse();

        if (clarification.getImage() != null)
            this.image = new ImageDto(clarification.getImage());
        if (clarification.getCreationDate() != null)
            this.creationDate = clarification.getCreationDate().format(formatter);
            this.creationDate = clarification.getAnswerDate().format(formatter);

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public Integer getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(Integer numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherResponse() {
        return teacherResponse;
    }

    public void setTeacherResponse(String teacherResponse) {
        this.teacherResponse = teacherResponse;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", numberOfAnswers=" + numberOfAnswers +
                ", status='" + status + '\'' +
                ", image=" + image +
                '}';
    }*/
}
