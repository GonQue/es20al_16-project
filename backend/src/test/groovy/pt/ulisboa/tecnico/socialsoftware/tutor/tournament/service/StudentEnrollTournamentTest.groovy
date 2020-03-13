package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

class StudentEnrollTournamentTest extends Specification {

    public static final String COURSE_NAME1 = "Course name 1"
    public static final String COURSE_NAME2 = "Course name 2"
    public static final String TOURNAMENT_NAME = "Tournament name"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"



    def tournamentService
    def tournament
    def tournamentDto
    def course1
    def courseExecution1
    def user
    def user2
    def userDto
    def user2Dto

    def setup(){
        tournamentService = new TournamentService()

        course1 = new Course(COURSE_NAME1, Course.Type.TECNICO)
        courseExecution1 = new CourseExecution(course1, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution1.setId(1)

        tournament = new Tournament()
        tournament.setCourseExecution(courseExecution1)
        tournamentDto = new TournamentDto()
        tournamentDto.setId(1)
        user = new User()
        user.setId(1)
        user.setKey(1)
        user.setRole(User.Role.STUDENT)


        userDto = new UserDto(user)

        //tournamentDto.setEnrolled(new ArrayList<>(Arrays.asList(userDto)))

    }

    def "enroll student in the tournament"(){
        // a student, with name and course, enrolls on a open tournament
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a student in a course execution"
        user.addCourse(courseExecution1)

        when:
        def result = tournamentService.enrollStudent(tournamentDto, userDto)

        then:
        result.getId() == 1
        result.getEnrolled()!=null
        result.getEnrolled().size()==1
        result.getEnrolled().contains(userDto)
        result.getEnrolled().get(0).getId()==1

    }

    def "student enrolls in a tournament that has students enrolled already"(){
        given:"a closed tournament"
        tournamentDto.setName(TOURNAMENT_NAME)

        and:"a student in a course execution"
        user.addCourse(courseExecution1)

        and: "another student enrolled in the tournament"
        user2 = new User()
        user2.setId(2)
        user2.setKey(2)
        user2.setRole(User.Role.STUDENT)
        user2.addCourse(courseExecution1)
        user2Dto = new UserDto(user2)
        tournamentDto.setEnrolled(new ArrayList<>(Arrays.asList(user2Dto)))

        when:
        def result = tournamentService.enrollStudent(tournamentDto, userDto)

        then:
        result.getId() == 1
        result.getEnrolled()!=null
        result.getEnrolled().size()==2
        result.getEnrolled().contains(userDto)
        result.getEnrolled().contains(user2Dto)

    }

    def "user enrolling is not a student"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"teacher"
        user.setRole(User.Role.TEACHER)

        when:
        tournamentService.enrollStudent(tournamentDto, userDto)
        then:
        thrown(TutorException)
    }

    def "student already in tournament"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"student enrolled in the tournament"
        user.addCourse(courseExecution1)
        tournamentDto.setEnrolled(new ArrayList<>(Arrays.asList(userDto)))
        when:
        tournamentService.enrollStudent(tournamentDto, userDto)
        then:
        thrown(TutorException)
    }

    def "student not in course execution of tournament"(){
        given:"a tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        and:"a student not in the same course execution"
        Course course2 = new Course(COURSE_NAME2, Course.Type.TECNICO)
        CourseExecution courseExecution2 = new CourseExecution(course2, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution2.setId(2)
        user.addCourse(courseExecution2)

        when:
        tournamentService.enrollStudent(tournamentDto, userDto)

        then:
        thrown(TutorException)
    }

    def "tournament is closed"(){
        given:"a closed tournament"
        tournamentDto.setName(TOURNAMENT_NAME)
        tournamentDto.setStatus(Tournament.Status.CLOSED.name())

        and:"a student in a course execution"
        user.addCourse(courseExecution1)

        when:
        tournamentService.enrollStudent(tournamentDto, userDto)

        then:
        thrown(TutorException)

    }
}
