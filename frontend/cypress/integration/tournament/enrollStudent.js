describe('Student walkthrough', () => {
  let tournamentName, startDay, endDay, nextMonth, pickQuestionNumber, topics;

  beforeEach(() => {
    cy.demoStudentLogin()
    tournamentName = 'Demo Tournament';
    topics = ['Adventure Builder'];
    startDay = 20;
    endDay = 20;
    nextMonth = true;
    pickQuestionNumber = true;
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('Login creates tournament and enrolls', () => {
    cy.createTournament(tournamentName,topics, startDay, endDay, nextMonth, pickQuestionNumber)
    cy.enrollStudent(tournamentName)
    //cy.get('enrollButton').should('be.disabled')
    cy.removeTournamentFromDB(tournamentName)
  });
/*
  it('Login creates tournament, enrolls, and tries to enroll again', () => {
    cy.createTournament(tournamentName,topics, startDay, endDay, nextMonth, pickQuestionNumber)
    cy.enrollStudent(tournamentName)

    cy.log('tries to enroll when already enrolled')
    cy.enrollStudent(tournamentName)

    cy.closeErrorMessage()

    cy.log('close dialog')
    cy.get('[data-cy="cancelButton"]').click()

    cy.removeTournamentFromDB(tournamentName)//not needed
  });*/

});
