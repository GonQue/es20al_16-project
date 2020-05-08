describe('Dashboard Walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();

  });

  afterEach(() => {
    cy.contains('Logout').click();
  });
  it('dashboard shows 0 tournaments, 0 public', () => {
    cy.checkTournamentStats('0','0');
  });

  it('dashboard shows 2 tournaments, 1 joined', () => {
    cy.demoStudentLogin();
    cy.contains('Tournaments').click();
    cy.createTournament('Demo Tournament 1', ['Availability'], 20, 21, false, true, true);
    cy.createTournament('Demo Tournament 2', ['Availability'], 20, 21, false, true, true);
    cy.enrollStudent('Demo Tournament 1');
    cy.checkTournamentStats('2','1');
    cy.deleteTournament('Demo Tournament 1');
    cy.deleteTournament('Demo Tournament 2');
    cy.get('[data-cy="Tournaments"]').click();
  });

  it('toggleDashboard to Public, check if still public', () => {
    cy.addPrivateDashboardToDemoStudent();
    cy.toggleDashboardPrivacy();
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.checkDashboardPrivacy('PUBLIC');
    cy.toggleDashboardPrivacy();
  });

  it('dashboard shows 0 clarifications, 0 public', () => {
    cy.checkClarificationStats('0','0');
  });

 it('dashboard shows 2 clarifications, 1 public', () => {
    cy.answerQuiz();
    cy.showQuizAnswer();
    cy.createClarificationQuestion('test question');
    cy.showQuizAnswer();
    cy.createClarificationQuestion('test question 2');
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.changeClarificationAvailability(/^test question 2$/)
    cy.contains('Management').click();
    cy.contains('Logout').click({force:true});
    cy.demoStudentLogin();
    cy.checkClarificationStats('2','1');
   cy.deleteClarificationQuestion(/^test question$/);
   cy.deleteClarificationQuestion(/^test question 2$/);
   cy.get('[data-cy="Questions"]').click();
  });




});