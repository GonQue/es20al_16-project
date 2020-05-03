describe('Student walkthrough - Clarifications', () => {
  before(() => {
    cy.demoStudentLogin();
    cy.answerQuiz();
  });

  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.get('[data-cy="Questions"]').click();
    cy.contains('Logout').click();
  });

  it('type some text and create a clarification question; delete the clarification question', () => {
    cy.showQuizAnswer();
    cy.createClarificationQuestion('I did not understand the question.');
    cy.successMessage();
    cy.deleteClarificationQuestion('I did not understand the question.');
  });

  it('type some text and cancel the creation of clarification question; nothing to be deleted', () => {
    cy.showQuizAnswer();
    cy.cancelCreateClarificationQuestion(
      'I did not understand the question. CANCEL VERSION'
    );
    cy.nothingToBeDeleted('I did not understand the question. CANCEL VERSION');
  });

  it('type some text and cancel; type again and create a clarification question; delete the clarification question', () => {
    cy.showQuizAnswer();
    cy.cancelCreateClarificationQuestion(
      'I did not understand the question. CANCEL VERSION'
    );
    cy.createClarificationQuestion('I did not understand the question.');
    cy.successMessage();
    cy.deleteClarificationQuestion('I did not understand the question.');
  });

  it('do not type and cancel; type and create a clarification question; delete the clarification question', () => {
    cy.showQuizAnswer();
    cy.cancelCreateClarificationQuestion();
    cy.createClarificationQuestion('I did not understand the question.');
    cy.successMessage();
    cy.deleteClarificationQuestion('I did not understand the question.');
  });

  it('create two clarification questions; delete both', () => {
    cy.showQuizAnswer();
    cy.cancelCreateClarificationQuestion();
    cy.createClarificationQuestion('I did not understand the question. FIRST');
    cy.successMessage();
    cy.createClarificationQuestion('I did not understand the question. SECOND');
    cy.successMessage();
    cy.deleteClarificationQuestion('I did not understand the question. FIRST');
    cy.deleteClarificationQuestion('I did not understand the question. SECOND');
  });
});
