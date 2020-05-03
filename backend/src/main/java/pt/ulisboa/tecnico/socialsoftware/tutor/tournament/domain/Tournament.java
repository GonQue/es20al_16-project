package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@Table(name = "tournaments")
public class Tournament {
    public enum Status {CREATED, STARTED, CLOSED}

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "name")
    private String name = "Tournament Name";

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "number_of_questions")
    private Integer numberOfQuestions;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "tournament_creator_id")
    private User creator;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Topic> topics = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Set<User> enrolled = new HashSet<>();
  

    public Tournament(){}

    public Tournament(User creator, CourseExecution courseExecution, Quiz quiz, TournamentDto tournamentDto){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if(tournamentDto.getName() == null || tournamentDto.getName().trim().isEmpty()){
            throw new TutorException(TOURNAMENT_NAME_INVALID);
        }


        if(tournamentDto.getStartDate()==null || tournamentDto.getEndDate()==null){
            startDate = LocalDateTime.now().plusHours(1);
            endDate = LocalDateTime.now().plusHours(2);
        } else {
            this.startDate = LocalDateTime.parse(tournamentDto.getStartDate(), formatter);
            this.endDate = LocalDateTime.parse(tournamentDto.getEndDate(), formatter);
        }

        if (startDate.isAfter(endDate)){
            throw new TutorException(TOURNAMENT_TIME_INVALID);
        }

        if(tournamentDto.getNumberOfQuestions() <= 0){
            throw new TutorException(TOURNAMENT_NUMBER_OF_QUESTIONS_INVALID);
        }

        this.id = tournamentDto.getId();
        this.name = tournamentDto.getName();
        this.creator = creator;
        this.numberOfQuestions = tournamentDto.getNumberOfQuestions();
        this.courseExecution = courseExecution;
        this.quiz = quiz;
        this.status = Status.CREATED;
    }

    public void addStudent(User user){
        enrolled.add(user);
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }


    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartDate() { return startDate; }

    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }

    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getNumberOfQuestions() { return numberOfQuestions; }

    public void setNumberOfQuestions(Integer numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }

    public User getCreator() { return creator; }

    public void setCreator(User creator) { this.creator = creator; }

    public Quiz getQuiz() { return quiz; }

    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Set<Topic> getTopics() { return topics; }

    public void setTopics(Set<Topic> topics) { this.topics = topics; }

    public Set<User> getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Set<User> enrolled) {
        this.enrolled = enrolled;
    }
}