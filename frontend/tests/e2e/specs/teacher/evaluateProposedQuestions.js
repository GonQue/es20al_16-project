describe('Evaluation', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.openProposeQuestionStudentMenu();
    cy.createProposedQuestion('TEST', 'Question', 'Options', 1);
    cy.showProposedQuestion('TEST');
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.openProposedQuestionsMenu();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('evaluate awaiting proposed question', () => {
    cy.evaluate('TEST', 'APPROVED', 'Justification');
    cy.get('[data-cy="search"').clear();
    cy.evaluate('TEST', 'AWAITING', ' ');
    cy.get('[data-cy="search"').clear();

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.openProposeQuestionStudentMenu();
    cy.deleteProposedQuestion('TEST');
  });

  it('evaluate approved proposed question', () => {
    cy.evaluate('TEST', 'APPROVED', 'Justification');
    cy.get('[data-cy="search"').clear();
    cy.evaluate('TEST', 'REJECTED', 'Justification');
    cy.get('[data-cy="search"').clear();

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.openProposeQuestionStudentMenu();
    cy.deleteProposedQuestion('TEST');
  });

  it('reject awaiting proposed question without justification', () => {
    cy.evaluate('TEST', 'REJECTED', ' ');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.openProposeQuestionStudentMenu();
    cy.deleteProposedQuestion('TEST');
  });

  it('turn an approved proposed question available ', () => {
    cy.evaluate('TEST', 'APPROVED', 'Justification');
    cy.get('[data-cy="search"').clear();
    cy.turnAvailable('TEST');
    cy.get('[data-cy="search"').clear();
  })
});
