describe('Student walkthrough', () => {
  let tournamentName, startDay, endDay, nextMonth, pickQuestionNumber, topics;

  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Tournaments')
      .click()
      .wait(5000);

    tournamentName = 'Demo Tournament 1';
    tournamentName2 = 'Demo Tournament 2';

    topics = ['Availability'];
    startDay = 20;
    endDay = 21;
    startMonthBefore = false;
    endNextMonth = true;
    pickQuestionNumber = true;
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Login creates tournament, checks creation and deletes', () => {
    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      startMonthBefore,
      endNextMonth,
      pickQuestionNumber
    );

    cy.checkTournament(tournamentName, 1);
    cy.deleteTournament(tournamentName);

    cy.removeTournamentFromDB(tournamentName);

  });

  it('Login creates tournament with wrong date', () => {
    endNextMonth = false;
    endDay = 10;

    cy.log('try to create with wrong date');
    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      startMonthBefore,
      endNextMonth,
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
      startMonthBefore,
      endNextMonth,
      pickQuestionNumber
    );

    cy.closeErrorMessage();

    cy.log('close dialog');
    cy.get('[data-cy="cancelButton"]').click();

    cy.removeTournamentFromDB(tournamentName)
  });

  it('Login creates 2 tournaments with multiple topics', () => {
    topics = ['Availability', 'Architectural Style', 'Chrome'];

    cy.createTournament(
      tournamentName,
      topics,
      startDay,
      endDay,
      startMonthBefore,
      endNextMonth,
      pickQuestionNumber
    );
    cy.createTournament(
      tournamentName2,
      topics,
      startDay,
      endDay,
      startMonthBefore,
      endNextMonth,
      pickQuestionNumber
    );

    cy.removeTournamentFromDB(tournamentName)
    cy.removeTournamentFromDB(tournamentName2)
  });
});
