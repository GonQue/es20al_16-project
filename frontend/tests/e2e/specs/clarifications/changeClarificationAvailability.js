describe('Teacher walkthrough - Make Clarification and its responses available to other students', () => {
  before(() => {
    cy.demoStudentLogin();
    //cy.answerQuiz();
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
    cy.demoTeacherLogin();
  });

  after(() => {
    cy.demoStudentLogin();
    cy.deleteClarificationQuestion('I did not understand the question.');
  });

  afterEach(() => {
    cy.contains('Management').click();
    cy.contains('Logout').click();
  });

  it('make clarification available to other students', () => {
    cy.changeClarificationAvailability('I did not understand the question.');
    cy.checkIfClarificationIsAvailable('I did not understand the question.');
  });

  it('make clarification unavailable to other students', () => {
    cy.changeClarificationAvailability('I did not understand the question.');
    cy.checkIfClarificationIsUnavailable('I did not understand the question.');
  });
});
