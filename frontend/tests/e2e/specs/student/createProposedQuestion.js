describe('Propose Question walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.contains('Questions').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates and deletes a proposed question', () => {
    cy.createProposedQuestion('Title_test', 'Content_test', 'Option', 1);
    cy.showProposedQuestion('Title_test');
    cy.deleteProposedQuestion('Title_test');
  });

  it('login creates a proposed question without title', () => {
    cy.createProposedQuestion(null, 'Content_test', 'Option', 2);

    cy.closeQuestionMessage();
    cy.get('[data-cy="cancelButton"]').click();
  });
});
