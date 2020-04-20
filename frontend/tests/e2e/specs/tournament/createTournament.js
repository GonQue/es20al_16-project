describe('Student walkthrough', () => {
  let tournamentName, startDay, endDay, nextMonth, pickQuestionNumber, topics;

  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Tournaments')
      .click()
      .wait(5000);

    tournamentName = 'Demo Tournament';
    topics = ['Adventure Builder'];
    startDay = 20;
    endDay = 20;
    nextMonth = true;
    pickQuestionNumber = true;
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Login creates tournament and checks creation', () => {
    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      nextMonth,
      pickQuestionNumber
    );

    cy.checkTournament(tournamentName, 1);
    cy.removeTournamentFromDB(tournamentName);
  });

  it('Login creates tournament with wrong date', () => {
    nextMonth = false;
    endDay = 10;

    cy.log('try to create with wrong date');
    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      nextMonth,
      pickQuestionNumber
    );

    cy.closeErrorMessage();

    cy.log('close dialog');
    cy.get('[data-cy="cancelButton"]').click();

    cy.removeTournamentFromDB(tournamentName)
  });

  it('Login creates tournament with 0 questions', () => {
    let pickQuestionNumber = false;

    cy.log('try to create with 0 questions');
    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      nextMonth,
      pickQuestionNumber
    );

    cy.closeErrorMessage();

    cy.log('close dialog');
    cy.get('[data-cy="cancelButton"]').click();

    cy.removeTournamentFromDB(tournamentName)
  });

  it('Login creates 2 tournaments with multiple topics', () => {
    topics = ['Adventure Builder', 'Architectural Style', 'Chrome'];

    cy.log('try to create with 0 questions');
    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      nextMonth,
      pickQuestionNumber
    );
    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      nextMonth,
      pickQuestionNumber
    );

    cy.removeTournamentFromDB(tournamentName)
  });
});
