package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;


import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {
   @Autowired
   private CourseExecutionRepository courseExecutionRepository;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private TournamentRepository tournamentRepository;

   @Autowired
   private TopicRepository topicRepository;

   @Autowired
   private QuizRepository quizRepository;

   @PersistenceContext
   EntityManager entityManager;

   @Transactional(isolation = Isolation.REPEATABLE_READ)
   public TournamentDto createTournament(int executionId, TournamentDto tournamentDto){
      CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

      Quiz quiz = getQuiz(tournamentDto);
      User creatorUser = getCreator(tournamentDto);
      checkCreatorCourse(courseExecution, creatorUser);

      Set<Topic> topics = getTopics(tournamentDto);

      Tournament tournament = createTournament(tournamentDto, courseExecution, quiz, creatorUser, topics);
      creatorUser.addTournament(tournament);
      return new TournamentDto(tournamentDto.getQuiz(), tournamentDto.getTopics(), tournament);

   }

   private Quiz getQuiz(TournamentDto tournamentDto) {
      if(tournamentDto.getQuiz()==null){
         throw new TutorException(TOURNAMENT_QUIZ_NOT_FOUND);
      }
      return quizRepository.findById(tournamentDto.getQuiz().getId()).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, tournamentDto.getQuiz().getId()));
   }

   private Set<Topic> getTopics(TournamentDto tournamentDto) {
      if(tournamentDto.getTopics() == null){ tournamentDto.setTopics(new ArrayList<>(Arrays.asList())); }

      Set<Topic> topics = new HashSet<>();
      List<TopicDto> topicsDto = tournamentDto.getTopics();
      for(TopicDto topicDto: topicsDto){
         Topic topic = topicRepository.findById(topicDto.getId()).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicDto.getId()));
         topics.add(topic);
      }

      return topics;
   }

   private Tournament createTournament(TournamentDto tournamentDto, CourseExecution courseExecution, Quiz quiz, User creatorUser, Set<Topic> topics) {
      Tournament tournament = new Tournament(creatorUser, courseExecution, quiz, tournamentDto);
      tournament.setTopics(topics);

      entityManager.persist(tournament);
      return tournament;
   }

   private void checkCreatorCourse(CourseExecution courseExecution, User creatorUser) {
      if (!creatorUser.getCourseExecutions().stream().anyMatch(ce -> ce.getId().equals(courseExecution.getId()))) {
         throw new TutorException(TOURNAMENT_CREATOR_NOT_ENROLLED);
      }
   }

   private User getCreator(TournamentDto tournamentDto) {
      if(tournamentDto.getCreator()==null){
         throw new TutorException(TOURNAMENT_NO_CREATOR);
      }
      User creatorUser = userRepository.findById(tournamentDto.getCreator().getId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, tournamentDto.getCreator().getId()));
      if(creatorUser.getRole() != User.Role.STUDENT){
         throw new TutorException(TOURNAMENT_CREATOR_NOT_STUDENT);
      }
      return creatorUser;
   }
}


      /*Tournament tournament = new Tournament(user, tournamentDto);
      tournament.setCourseExecution(courseExecution);
      tournament.setTopics(topics);
      tournament.setQuiz(quiz);

      entityManager.persist(tournament);

      return new TournamentDto(tournamentDto.getQuiz(), topicsDto, tournament);


   }*/

   @Transactional(isolation = Isolation.REPEATABLE_READ)
   public TournamentDto enrollStudent(TournamentDto tournamentDto, UserDto studentDto) {
      Tournament tournament = tournamentRepository.findById(tournamentDto.getId()).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentDto.getId()));
      User user = getUser(tournamentDto, studentDto);

      checkUserInCourseExecution(user, tournament.getCourseExecution(), USER_NOT_IN_COURSE_EXECUTION);
      if (tournament.getStatus() == Tournament.Status.CLOSED) { throw new TutorException(TOURNAMENT_CLOSED); }

      enrollStud(tournament, user);

      List<UserDto> enrolled = getUserDtos(tournament);
      TournamentDto tDto = new TournamentDto(tournament);
      tDto.setEnrolled(enrolled);

      return tDto;

   }

   private void enrollStud(Tournament tournament, User user) {
      if(tournament.getEnrolled().stream().anyMatch(u -> u.getId().equals(user.getId()))){
         throw new TutorException(STUDENT_ALREADY_ENROLLED);
      }
      tournament.addStudent(user);
      user.enrollTournament(tournament);
   }

   private void checkUserInCourseExecution(User user, CourseExecution courseExecution, ErrorMessage em) {
      if (!user.getCourseExecutions().stream()
              .anyMatch(t -> t.getId().equals(courseExecution.getId()))) {
         throw new TutorException(em);
      }
   }

   private List<UserDto> getUserDtos(Tournament tournament) {
      List<UserDto> enrolled = new ArrayList<>();
      Set<User> users = tournament.getEnrolled();
      for(User u: users){
         UserDto userDto = new UserDto(u);
         enrolled.add(userDto);
      }
      return enrolled;
   }

   private User getUser(TournamentDto tournamentDto, UserDto studentDto) {
      if(studentDto==null){ throw new TutorException(TOURNAMENT_NO_STUDENT_TO_ENROLL);}
      User user = userRepository.findById(studentDto.getId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, tournamentDto.getCreator().getId()));
      if (user.getRole() != User.Role.STUDENT) {
         throw new TutorException(TOURNAMENT_ENROLLED_NOT_STUDENT);
      }
      if(user.getUsername() == null || user.getUsername().trim().isEmpty()){
         throw new TutorException(INVALID_USERNAME);
      }
      return user;
   }
}
