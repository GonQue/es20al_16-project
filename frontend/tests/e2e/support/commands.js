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
  cy.visit('/');
  cy.get('[data-cy="demoAdminLoginButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
});

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

// Student

Cypress.Commands.add(
  'createTournament',
  (name, topics, day1, day2, startMonthBefore, nextMonth, pickQuestionNumber) => {
    cy.get('[data-cy="createButton"]').click();

    //Name
    cy.get('[data-cy="Name"]').type(name);

    //Start date
    cy.get('[data-cy=startDate]').click();

    if(startMonthBefore)
      cy.get('i[class="v-icon notranslate mdi mdi-chevron-left theme--light"]').click().wait(500);

    cy.get('button')
      .contains(day1)
      .click()
      .wait(500);
    cy.contains('OK')
      .click()
      .wait(500);

    //End date
    cy.get('[data-cy=endDate]').click();
    if (nextMonth)
      cy.get(
        'i[class="v-icon notranslate mdi mdi-chevron-right theme--light"]:visible'
      )
        .click()
        .wait(500);
    cy.get('button:visible')
      .contains(day2)
      .click()
      .wait(500);
    cy.get('button:visible')
      .contains('OK')
      .click();

    //Number of questions
    if (pickQuestionNumber) cy.get('[data-cy=numberOfQuestions]').get('label').contains('Number of questions').type('{rightarrow}');


    //Topics
    cy.get('[data-cy="topics"]').click();
    topics.forEach(function(topics) {
      cy.contains(topics)
        .click()
        .wait(100);
    });

    //Save
    cy.get('[data-cy="saveButton"]')
      .click()
      .wait(200);
  }
);

Cypress.Commands.add('enrollStudent', name => {
  cy.contains(name)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="enrollButton"]')
    .click();
});
Cypress.Commands.add('answerQuestions', name => {
  cy.contains(name).parent().children().find('[data-cy="joinButton"]').click()

  cy.get('[data-cy="EndQuiz"]').click();
  cy.get('[data-cy="ImSure"]').click();
});


Cypress.Commands.add('getTopics', name => {
  cy.get('td[class="text-start"]')
    .first()
    .click();
});

Cypress.Commands.add('checkTournament', (name, numberOfTournaments) => {
  cy.contains(name)
    .parent()
    .should('have.length', numberOfTournaments)
    .children()
    .should('have.length', 7);
});

Cypress.Commands.add('insertStudentInTournament', (name, enrolled_id) => {
  cy.exec(
    'PGPASSWORD=$dbpass psql -d tutordb -U $dbUser -h localhost -c "Insert into tournaments_enrolled(tournaments_enrolled_id, enrolled_id) ' +
    '                                                                        select id, $enrolled_id from tournaments where name=\'$name\';\n"',
    { env: { name: name , enrolled_id: enrolled_id, dbpass: Cypress.env('dbpass'), dbUser: Cypress.env('dbUser')} }
  );
});

Cypress.Commands.add('removeTournamentFromDB', name => {
  cy.exec(
    'PGPASSWORD=$dbpass psql -d tutordb -U $dbUser -h localhost -c "DELETE FROM tournaments_topics WHERE tournaments_id in(select id from tournaments where name=\'$name\')"',
    { env: { name: name , dbpass: Cypress.env('dbpass'), dbUser: Cypress.env('dbUser')} }
  );
  cy.exec(
    'PGPASSWORD=$dbpass psql -d tutordb -U $dbUser -h localhost -c "DELETE FROM tournaments_enrolled WHERE tournaments_enrolled_id in(select id from tournaments where name=\'$name\')"',
    { env: { name: name , dbpass: Cypress.env('dbpass'), dbUser: Cypress.env('dbUser')} }
  );
  cy.exec(
    'PGPASSWORD=$dbpass psql -d tutordb -U $dbUser -h localhost -c "DELETE FROM tournaments WHERE name=\'$name\'"',
    { env: { name: name , dbpass: Cypress.env('dbpass'), dbUser: Cypress.env('dbUser')} }
  );
});

Cypress.Commands.add('answerQuiz', () => {
  cy.get('[data-cy="Quizzes"]').click();
  cy.get('[data-cy="CreateQuiz"]').click();
  cy.get('[data-cy="CreateQuizButton"]').click();
  cy.get('[data-cy="EndQuiz"]').click();
  cy.get('[data-cy="ImSure"]').click();
});

Cypress.Commands.add('showQuizAnswer', clarificationQuestion => {
  cy.get('[data-cy="Quizzes"]').click();
  cy.get('[data-cy="SolvedQuizzes"]').click();
  cy.get('[data-cy="SolvedQuiz"]')
    .first()
    .click();
});

Cypress.Commands.add('createClarificationQuestion', clarificationQuestion => {
  cy.get('[data-cy="ClarificationButton"]').click();
  cy.get('[data-cy="ClarificationContent"]')
    .clear()
    .type(clarificationQuestion);
  cy.get('[data-cy="SaveButton"]').click();
});

Cypress.Commands.add('deleteClarificationQuestion', clarificationQuestion => {
  cy.contains('Questions').click();
  cy.get('[data-cy="Clarifications"]').click();
  cy.contains(clarificationQuestion)
    .first()
    .parent()
    .should('have.length', 1)
    .children()
    .find('[data-cy="DeleteClarificationIcon"]')
    .click();
});

Cypress.Commands.add('cancelCreateClarificationQuestion', () => {
  cy.get('[data-cy="ClarificationButton"]').click();
  cy.get('[data-cy="CancelButton"]').click();
});

Cypress.Commands.add(
  'cancelCreateClarificationQuestion',
  clarificationQuestion => {
    cy.get('[data-cy="ClarificationButton"]').click();
    cy.get('[data-cy="CancelButton"]').click();
  }
);

Cypress.Commands.add('nothingToBeDeleted', clarificationQuestion => {
  cy.contains('Questions').click();
  cy.get('[data-cy="Clarifications"]').click();
  cy.should('not.contain', clarificationQuestion);
});

Cypress.Commands.add('successMessage', (name, acronym, academicTerm) => {
  cy.get('[data-cy="SuccessMessage"]')
    .find('button')
    .click();
});

// Student - get public or own clarifications

Cypress.Commands.add('showPublicClarifications', content => {
  cy.get('[data-cy="ShowPublicClarifications"]').click();
  cy.contains(content);
});

Cypress.Commands.add('assignPublicClarificationToAnotherStudent', content => {
  cy.exec(
    'PGPASSWORD=jrd1999 psql -d tutordb -U joaodias -h localhost -c "INSERT INTO users VALUES (999999999, \'2019-10-18 21:17:28.460416\', \'n\', \'r\', \'u\', 1, 1, 1, 1, \'e\', \'2019-10-18 21:17:28.460416\', 999999999, 1, 1, 1, 1, 1)"'
  );
  cy.exec(
    'PGPASSWORD=jrd1999 psql -d tutordb -U joaodias -h localhost -c "UPDATE clarifications SET user_id = 999999999, available_to_other_students = \'t\' WHERE content = \'$content\'"',
    { env: { content: content } }
  );
});

Cypress.Commands.add('deletePublicClarification', content => {
  cy.exec(
    'PGPASSWORD=jrd1999 psql -d tutordb -U joaodias -h localhost -c "DELETE FROM clarification_responses WHERE clarification_id IN (SELECT id FROM clarifications WHERE content=\'$content\')"',
    { env: { content: content } }
  );
  cy.exec(
    'PGPASSWORD=jrd1999 psql -d tutordb -U joaodias -h localhost -c "DELETE FROM clarifications WHERE content=\'$content\'"',
    { env: { content: content } }
  );
});

Cypress.Commands.add('deletePublicStudent', () => {
  cy.exec(
    'PGPASSWORD=jrd1999 psql -d tutordb -U joaodias -h localhost -c "DELETE FROM users WHERE id=999999999"'
  );
});

Cypress.Commands.add(
  'checkPublicResponses',
  (clarificationContent, responseContent) => {
    cy.contains(clarificationContent)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 4)
      .find('[data-cy="ShowResponses"]')
      .click({ force: true });
    cy.contains(responseContent);
  }
);

Cypress.Commands.add(
  'checkNoPublicResponses',
  (clarificationContent, responseContent) => {
    cy.contains(clarificationContent)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 4)
      .find('[data-cy="ShowResponses"]')
      .click({ force: true });
    cy.contains(responseContent).should('not.exist');
  }
);

// Student - ask for additional clarification

Cypress.Commands.add('askForAdditionalClarification', clarificationContent => {
  cy.contains('Questions').click();
  cy.get('[data-cy="Clarifications"]').click();
  cy.contains(clarificationContent)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="ShowResponses"]')
    .click({ force: true });
  cy.get('[data-cy="askForAdditionalClarificationButton"').click();
  cy.get('[data-cy="SubmitButton"').click();
});

Cypress.Commands.add('checkAdditionalClarification', clarificationContent => {
  cy.contains('Questions').click();
  cy.contains('Clarifications').click();
  cy.contains(clarificationContent)
    .get('[data-cy="NeedClarificationIcon"')
    .should('have.class', 'mdi-comment-remove');
});

Cypress.Commands.add(
  'cancelAskForAdditionalClarification',
  clarificationContent => {
    cy.contains('Questions').click();
    cy.get('[data-cy="Clarifications"]').click();
    cy.contains(clarificationContent)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="ShowResponses"]')
      .click({ force: true });
    cy.get('[data-cy="askForAdditionalClarificationButton"').click();
    cy.get('[data-cy="CancelButton"').click();
  }
);

Cypress.Commands.add('checkNoAdditionalClarification', clarificationContent => {
  cy.contains('Questions').click();
  cy.contains('Clarifications').click();
  cy.contains(clarificationContent)
    .get('[data-cy="NeedClarificationIcon"')
    .should('have.class', 'mdi-comment-check');
});

Cypress.Commands.add('openProposeQuestionStudentMenu', () => {
  cy.contains('Questions').click();
  cy.get('[data-cy="ProposeQuestion"').click();
});

Cypress.Commands.add(
  'createProposedQuestion',
  (title, content, optionsText, correct) => {
    cy.get('[data-cy="proposeQuestionButton"]').click();

    if (title) cy.get('[data-cy="Title"]').type(title, { force: true });

    cy.get('[data-cy="Question"]').type(content);
    cy.contains('Correct ' + correct)
      .parent()
      .children()
      .find('[data-cy="CorrectOption"]')
      .click({ force: true });
    cy.get('[data-cy="Option"]')
      .should('have.length', 4)
      .each($el => cy.wrap($el).type(optionsText, { force: true }));
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('showProposedQuestion', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="showProposedQuestion"]')
    .click();
  cy.get('[data-cy="closeButton"]').click();
});

Cypress.Commands.add('deleteProposedQuestion', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="deleteProposedQuestion"]')
    .click({ force: true });
});

Cypress.Commands.add('closeQuestionMessage', () => {
  cy.contains('Question must have title and content')
    .parent()
    .find('button')
    .click();
});

// Teacher

Cypress.Commands.add('checkClarificationQuestions', () => {
  cy.contains('Management').click();
  cy.contains('Clarifications').click();
});

Cypress.Commands.add(
  'createClarificationResponse',
  (questionContent, responseContent) => {
    cy.contains(questionContent)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 8)
      .find('[data-cy="AnswerClarification"]')
      .click();
    cy.get('[data-cy="TeacherResponse"]')
      .clear()
      .type(responseContent);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add(
  'deleteClarificationResponse',
  (questionContent, responseContent) => {
    cy.contains(questionContent)
      .parent()
      .should('have.length', 1)
      .children()
      .find('[data-cy="ShowResponses"]')
      .click({ force: true });
    cy.contains(responseContent)
      .first()
      .parent()
      .should('have.length', 1)
      .find('[data-cy="DeleteClarificationResponseIcon"]')
      .click();
  }
);

Cypress.Commands.add('openProposedQuestionsMenu', () => {
  cy.contains('Management').click();
  cy.get('[data-cy="ProposedQuestions"]').click();
});

Cypress.Commands.add('evaluate', (title, evaluation, justification) => {
  cy.get('[data-cy="search"').type(title);
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="evaluate"]')
    .click();
  cy.get('[data-cy="justification"]').clear();
  if (justification !== ' ')
    cy.get('[data-cy="justification"]').type(justification);
  cy.get('[data-cy="evaluation"]').click({ force: true });
  cy.contains(evaluation).click({ force: true });
  cy.get('[data-cy="saveButton"]').click();
});

// Teacher - change clarification availability

Cypress.Commands.add(
  'changeClarificationAvailability',
  clarificationContent => {
    cy.contains('Management').click();
    cy.contains('Clarifications').click();
    cy.contains(clarificationContent)
      .parent()
      .within(() => {
        cy.get('[data-cy="AvailabilityDiv"]')
          .first()
          .click();
      });
  }
);

Cypress.Commands.add(
  'checkIfClarificationIsAvailable',
  clarificationContent => {
    cy.contains('Management').click();
    cy.contains('Clarifications').click();
    cy.contains(clarificationContent)
      .parent()
      .within(() => {
        cy.get('[data-cy="AvailabilityDiv"]')
          .first()
          .within(() => {
            cy.get('[data-cy="AvailabilitySwitch"]').should('be.checked');
          });
      });
  }
);

Cypress.Commands.add(
  'checkIfClarificationIsUnavailable',
  clarificationContent => {
    cy.contains('Management').click();
    cy.contains('Clarifications').click();
    cy.contains(clarificationContent)
      .parent()
      .within(() => {
        cy.get('[data-cy="AvailabilityDiv"]')
          .first()
          .within(() => {
            cy.get('[data-cy="AvailabilitySwitch"]').should('not.be.checked');
          });
      });
  }
);

Cypress.Commands.add('checkClarificationStats', (clarifs, publicClarifs) => {
  cy.contains('Stats').click();
  cy.get('[data-cy="totalClarificationQuestions"]')
    .should('have.text',clarifs)
  cy.get('[data-cy="totalPublicClarificationQuestions"]')
    .should('have.text',publicClarifs)
});

Cypress.Commands.add('toggleDashboardPrivacy', () => {
  cy.contains('Stats').click();
  cy.get('[data-cy="privacyButton"]')
    .click();
});

Cypress.Commands.add('addPrivateDashboardToDemoStudent', () => {
  cy.exec(
    'PGPASSWORD=a psql -d tutordb -U a -h localhost -c "UPDATE users SET public_dashboard = false WHERE id = 676"'
  );
});

Cypress.Commands.add('checkDashboardPrivacy', (content) => {
  cy.contains('Stats').click();
  cy.get('[data-cy="privacyInfo"]')
    .contains(/PUBLIC/)
});
