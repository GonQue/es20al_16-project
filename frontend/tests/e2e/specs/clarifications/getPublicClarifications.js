describe('Student walkthrough - Get public clarifications or own clarifications', () => {
  before(() => {
    //cy.demoStudentLogin();
    //cy.answerQuiz();
  });

  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.get('[data-cy="Questions"]').click();
    cy.contains('Logout').click();
    cy.deletePublicClarification('own clarification test');
    cy.deletePublicClarification('public clarification test');
    cy.deletePublicStudent();
  });

  it('get own clarfication with no responses', () => {
    cy.showQuizAnswer();
    cy.createClarificationQuestion('own clarification test');
    cy.successMessage();
    cy.showPublicClarifications('own clarification test');
    cy.checkNoPublicResponses(
      'own clarification test',
      'public clarification test answer'
    );
  });

  it('get public clarfication from another student with no responses', () => {
    cy.showQuizAnswer();
    cy.createClarificationQuestion('public clarification test');
    cy.successMessage();
    cy.assignPublicClarificationToAnotherStudent('public clarification test');
    cy.showPublicClarifications('public clarification test');
    cy.checkNoPublicResponses(
      'public clarification test',
      'public clarification test answer'
    );
  });

  it('get own clarfication with responses', () => {
    cy.showQuizAnswer();
    cy.createClarificationQuestion('own clarification test');
    cy.successMessage();
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.checkClarificationQuestions();
    cy.createClarificationResponse(
      'own clarification test',
      'public clarification test answer'
    );
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.showQuizAnswer();
    cy.showPublicClarifications('own clarification test');
    cy.checkPublicResponses(
      'own clarification test',
      'public clarification test answer'
    );
  });

  it('get public clarfication from another student with responses', () => {
    cy.showQuizAnswer();
    cy.createClarificationQuestion('public clarification test');
    cy.successMessage();
    cy.contains('Logout').click();
    cy.demoTeacherLogin();
    cy.checkClarificationQuestions();
    cy.createClarificationResponse(
      'public clarification test',
      'public clarification test answer'
    );
    cy.assignPublicClarificationToAnotherStudent('public clarification test');
    cy.contains('Logout').click();
    cy.demoStudentLogin();
    cy.showQuizAnswer();
    cy.showPublicClarifications('public clarification test');
    cy.checkPublicResponses(
      'public clarification test',
      'public clarification test answer'
    );
  });
});
