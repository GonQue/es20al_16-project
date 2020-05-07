describe('Dashboard Walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();

  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('toggleDashboard to Public, check if still public', () => {
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