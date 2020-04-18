describe('Clarification Response Tests', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
    cy.answerQuiz()
    cy.showQuizAnswer()
    cy.createClarificationQuestion('test question')
    cy.showQuizAnswer()
    cy.createClarificationQuestion('test question 2')
    cy.contains('Logout').click()
    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.deleteClarificationQuestion(/^test question$/)
    cy.deleteClarificationQuestion(/^test question 2$/)
    cy.get('[data-cy="Clarifications"]').click()
    cy.contains('Logout').click()
  })

  it('create a clarification response deleted by the teacher', () => {
    cy.checkClarificationQuestions()
    cy.createClarificationResponse(/^test question$/,'test answer')
    cy.deleteClarificationResponse(/^test question$/, 'test answer')
    cy.contains('Management').click()
  });

  it('create a clarification response deleted by the student', () => {
    cy.checkClarificationQuestions()
    cy.createClarificationResponse(/^test question$/,'test answer')
    cy.contains('Management').click()
    cy.contains('Logout').click()
    cy.demoStudentLogin()
    cy.get('[data-cy="Clarifications"]').click()
    cy.deleteClarificationResponse(/^test question$/, 'test answer')
  });

  it('teacher creates two responses to two clarification questions then deletes', () => {
    cy.demoTeacherLogin()
    cy.checkClarificationQuestions()
    cy.createClarificationResponse(/^test question$/,'test answer')
    cy.createClarificationResponse(/^test question 2$/, 'test answer 2')
    cy.deleteClarificationResponse(/^test question$/, 'test answer')
    cy.checkClarificationQuestions()
    cy.deleteClarificationResponse(/^test question 2$/,'test answer 2')
    cy.contains('Management').click()
  });

});
