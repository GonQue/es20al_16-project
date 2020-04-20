describe('Evaluation', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Questions').click();
    cy.createProposedQuestion('TEST', 'Question', 'Options', 1);
    cy.showProposedQuestion('TEST');
    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.openProposedQuestionsMenu();
  });

  afterEach(() => {
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.contains('Questions').click();
    cy.deleteProposedQuestion('TEST');
    cy.contains('Logout').click();
  });

  it('evaluate awaiting proposed question', () => {
    cy.evaluate('TEST', 'APPROVED', 'Justification');
    cy.get('[data-cy="search"').clear();

    cy.evaluate('TEST', 'AWAITING', ' ');
  });

  it('evaluate approved proposed question', () => {
    cy.evaluate('TEST', 'APPROVED', 'Justification');
    cy.get('[data-cy="search"').clear();
    cy.evaluate('TEST', 'REJECTED', 'Justification');
    cy.get('[data-cy="search"').clear();
  });

  it('reject awaiting proposed question without justification', () => {
    cy.evaluate('TEST', 'REJECTED', ' ');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
  });
});
