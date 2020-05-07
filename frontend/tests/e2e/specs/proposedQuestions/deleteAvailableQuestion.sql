DELETE FROM proposed_questions WHERE question_id = (SELECT id FROM questions WHERE title = 'NEWTITLE');
DELETE FROM options WHERE question_id = (SELECT id FROM questions WHERE title = 'NEWTITLE'); 
DELETE FROM questions WHERE title = 'NEWTITLE';