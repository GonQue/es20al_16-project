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
Cypress.Commands.add('demoAdminLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
    cy.contains('Administration').click()
    cy.contains('Manage Courses').click()
})

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
})

Cypress.Commands.add('demoTeacherLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="teacherButton"]').click()
})

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('closeErrorMessage', () => {
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

Cypress.Commands.add('createProposedQuestion', (title, content, optionsText, correct) => {
    cy.get('[data-cy="proposeQuestionButton"]').click()

    if (title)
        cy.get('[data-cy="Title"]').type(title, { force: true })

    cy.get('[data-cy="Question"]').type(content)
    cy.contains('Correct ' + correct)
        .parent()
        .children()
        .find('[data-cy="CorrectOption"]')
        .click({ force: true })
    cy.get('[data-cy="Option"]').should('have.length', 4)
        .each(($el) => cy.wrap($el).type(optionsText, { force: true }))
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('showProposedQuestion', (title) => {
    cy.contains(title)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 8)
      .find('[data-cy="showProposedQuestion"]')
      .click()
    cy.get('[data-cy="closeButton"]').click()
})

Cypress.Commands.add('deleteProposedQuestion', (title) => {
    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 8)
        .find('[data-cy="deleteProposedQuestion"]')
        .click({ force: true })
})
Cypress.Commands.add('openProposedQuestionsMenu', () => {
    cy.contains('Management').click()
    cy.get('[data-cy="ProposedQuestions"]').click()
})

Cypress.Commands.add('evaluate', (title, evaluation, justification) => {
    cy.get('[data-cy="search"').type(title)
    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 8)
        .find('[data-cy="evaluate"]')
        .click()
    cy.get('[data-cy="justification"]').clear()
    if(justification !== ' ')
        cy.get('[data-cy="justification"]').type(justification)
    cy.get('[data-cy="evaluation"]').click({force: true})
    cy.contains(evaluation).click({force: true})
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('closeQuestionMessage', () => {
    cy.contains('Question must have title and content')
      .parent()
      .find('button')
      .click()
})