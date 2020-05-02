// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click()
})

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

// Student

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
})

Cypress.Commands.add('answerQuiz', () => {
    cy.get('[data-cy="Quizzes"]').click()
    cy.get('[data-cy="CreateQuiz"]').click()
    cy.get('[data-cy="CreateQuizButton"]').click()
    cy.get('[data-cy="EndQuiz"]').click()
    cy.get('[data-cy="ImSure"]').click()
})

Cypress.Commands.add('showQuizAnswer', (clarificationQuestion) => {
    cy.get('[data-cy="Quizzes"]').click()
    cy.get('[data-cy="SolvedQuizzes"]').click()
    cy.get('[data-cy="SolvedQuiz"]').first().click()
})

Cypress.Commands.add('createClarificationQuestion', (clarificationQuestion) => {
    cy.get('[data-cy="ClarificationButton"]').click()
    cy.get('[data-cy="ClarificationContent"]').clear().type(clarificationQuestion)
    cy.get('[data-cy="SaveButton"]').click()
})

Cypress.Commands.add('deleteClarificationQuestion', (clarificationQuestion) => {
    cy.get('[data-cy="Clarifications"]').click()
    cy.contains(clarificationQuestion)
        .first()
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 6)
        .find('[data-cy="DeleteClarificationIcon"]')
        .click()
})

Cypress.Commands.add('cancelCreateClarificationQuestion', () => {
    cy.get('[data-cy="ClarificationButton"]').click()
    cy.get('[data-cy="CancelButton"]').click()
})

Cypress.Commands.add('cancelCreateClarificationQuestion', (clarificationQuestion) => {
    cy.get('[data-cy="ClarificationButton"]').click()
    cy.get('[data-cy="CancelButton"]').click()
})

Cypress.Commands.add('nothingToBeDeleted', (clarificationQuestion) => {
    cy.get('[data-cy="Clarifications"]').click()
    cy.should('not.contain', clarificationQuestion)
})

Cypress.Commands.add('successMessage', (name, acronym, academicTerm) => {
    cy.get('[data-cy="SuccessMessage"]')
        .find('button')
        .click()
})

// Teacher

Cypress.Commands.add('demoTeacherLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="teacherButton"]').click()
})

Cypress.Commands.add('checkClarificationQuestions', () => {
    cy.contains('Management').click()
    cy.contains('Clarifications').click()
})

Cypress.Commands.add('createClarificationResponse', (questionContent,responseContent) => {
    cy.contains(questionContent)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 6)
        .find('[data-cy="AnswerClarification"]').click()
    cy.get('[data-cy="TeacherResponse"]').clear().type(responseContent)
    cy.get('[data-cy="saveButton"]').click()

})

Cypress.Commands.add('deleteClarificationResponse', (questionContent, responseContent) => {
    cy.contains(questionContent)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 6)
        .find('[data-cy="ShowResponses"]').click()
    cy.contains(responseContent)
        .first()
        .parent()
        .should('have.length', 1)
        .find('[data-cy="DeleteClarificationResponseIcon"]')
        .click()
})