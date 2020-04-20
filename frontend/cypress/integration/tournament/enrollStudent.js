describe('Student walkthrough', () => {
  let tournamentName, startDay, endDay, nextMonth, pickQuestionNumber, topics;

  beforeEach(() => {
    cy.demoStudentLogin()
    cy.contains('Tournaments').click().wait(5000)
    tournamentName1 = 'Demo Tournament 1';
    tournamentName2 = 'Demo Tournament 2';
    topics = ['Adventure Builder'];
    startDay = 20;
    endDay = 20;
    nextMonth = true;
    pickQuestionNumber = true;
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })


  it('Login creates tournament, open topics, and enrolls', () => {
    cy.createTournament(tournamentName1,topics, startDay, endDay, nextMonth, pickQuestionNumber)
    cy.getTopics(tournamentName1)//open
    cy.getTopics(tournamentName1)//close
    cy.enrollStudent(tournamentName1)
    cy.removeTournamentFromDB(tournamentName1)
  });

  it('Login creates two tournaments and enrolls in both', () => {
    cy.createTournament(tournamentName1,topics, startDay, endDay, nextMonth, pickQuestionNumber)
    cy.createTournament(tournamentName2,topics, startDay, endDay, nextMonth, pickQuestionNumber)
    cy.enrollStudent(tournamentName1)
    cy.enrollStudent(tournamentName2)
    cy.removeTournamentFromDB(tournamentName1)
    cy.removeTournamentFromDB(tournamentName2)
  });
});
