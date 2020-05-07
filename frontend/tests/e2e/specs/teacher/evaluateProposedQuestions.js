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

  it('evaluate proposed question', () => {
    cy.evaluate('TEST', 'APPROVED', 'Justification');
    cy.get('[data-cy="search"').clear();
    cy.evaluate('TEST', 'REJECTED', 'Justification');
    cy.get('[data-cy="search"').clear();
    cy.evaluate('TEST', 'AWAITING', ' ');
    cy.get('[data-cy="search"').clear();

    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.openProposeQuestionStudentMenu();
    cy.deleteProposedQuestion('TEST');
  });

  it('turn proposed question available ', () => {
    cy.evaluate('TEST', 'APPROVED', 'Justification');
    cy.get('[data-cy="search"').clear();
    cy.turnAvailable('TEST', 'NEWTITLE', 'NEWCONTENT');
    cy.get('[data-cy="search"').clear();
    cy.exec('PGPASSWORD=$dbpass psql -d tutordb -U $dbUser -h localhost < tests/e2e/specs/teacher/deleteAvailableQuestion.sql');
  })
});
