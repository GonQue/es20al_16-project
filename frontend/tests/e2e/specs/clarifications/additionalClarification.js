describe('Student walkthrough - Additional Clarifications', () => {
  before(() => {
    cy.demoStudentLogin();
    cy.answerQuiz();
    cy.showQuizAnswer();
    cy.createClarificationQuestion('I did not understand the question.');
    cy.successMessage();
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.checkClarificationQuestions();
    cy.createClarificationResponse(
      'I did not understand the question.',
      'test answer'
    );
  });

  beforeEach(() => {
    cy.demoStudentLogin();
  });

  after(() => {
    cy.demoStudentLogin();
    cy.deleteClarificationQuestion('I did not understand the question.');
  });

  afterEach(() => {
    cy.get('[data-cy="Questions"]').click();
    cy.contains('Logout').click();
  });

  it('ask for additional clarification, but cancel the request', () => {
    cy.cancelAskForAdditionalClarification(
      'I did not understand the question.'
    );
    cy.checkNoAdditionalClarification('I did not understand the question.');
  });

  it('ask for additional clarification and confirm', () => {
    cy.askForAdditionalClarification('I did not understand the question.');
    cy.successMessage();
    cy.checkAdditionalClarification('I did not understand the question.');
  });

  it('ask for additional clarification; teacher answer again', () => {
    cy.askForAdditionalClarification('I did not understand the question.');
    cy.successMessage();
    cy.demoTeacherLogin();
    cy.checkClarificationQuestions();
    cy.createClarificationResponse(
      'I did not understand the question.',
      'test answer'
    );
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.checkNoAdditionalClarification('I did not understand the question.');
  });

});
