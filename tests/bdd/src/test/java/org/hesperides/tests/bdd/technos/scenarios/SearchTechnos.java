package org.hesperides.tests.bdd.technos.scenarios;

import cucumber.api.java8.En;
import org.hesperides.core.presentation.io.TechnoIO;
import org.hesperides.tests.bdd.technos.TechnoBuilder;
import org.hesperides.tests.bdd.technos.TechnoClient;
import org.hesperides.tests.bdd.templatecontainers.builders.TemplateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.hesperides.tests.bdd.commons.StepHelper.assertOK;
import static org.junit.Assert.assertEquals;

public class SearchTechnos implements En {

    @Autowired
    private TechnoClient technoClient;
    @Autowired
    private TechnoBuilder technoBuilder;
    @Autowired
    private TemplateBuilder templateBuilder;

    private ResponseEntity<TechnoIO[]> responseEntity;

    public SearchTechnos() {

        Given("^a list of (\\d+) technos$", (final Integer nbTechnos) -> {
            for (int i = 0; i < nbTechnos; i++) {
                technoBuilder.withName("a-techno").withVersion("0.0." + i + 1);
                technoClient.create(templateBuilder.build(), technoBuilder.build());
            }
        });

        When("^I search for one specific techno$", () -> {
            responseEntity = technoClient.search("a-techno 0.0.3");
        });

        When("^I search for some of those technos$", () -> {
            responseEntity = technoClient.search("a-techno");
        });

        When("^I search for a techno that does not exist$", () -> {
            responseEntity = technoClient.search("nope");
        });

        Then("^the techno is found$", () -> {
            assertOK(responseEntity);
            assertEquals(1, responseEntity.getBody().length);
        });

        Then("^the list of techno results is limited to (\\d+) items$", (final Integer limit) -> {
            assertOK(responseEntity);
            assertEquals(limit.intValue(), responseEntity.getBody().length);
        });

        Then("^the list of techno results is empty$", () -> {
            assertOK(responseEntity);
            assertEquals(0, responseEntity.getBody().length);
        });
    }
}
