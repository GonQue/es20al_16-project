package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import spock.lang.Specification

class ListResponsesTest extends Specification {

    def clarificationService

    def setup() {
        clarificationService = new ClarificationService()
    }

    def 'associate one response with a clarficationQuestion and list it'() {
        // one response is associated with a clarficationQuestion and the response is listed
        expect: false;
    }

    def 'associate two responses to a clarficationQuestion and list them'() {
        // two responses are associated with a clarficationQuestion and the responses are listed
        expect: false;
    }
}
