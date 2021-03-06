/*
 *
 * This file is part of the Hesperides distribution.
 * (https://github.com/voyages-sncf-technologies/hesperides)
 * Copyright (c) 2016 VSCT.
 *
 * Hesperides is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, version 3.
 *
 * Hesperides is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package org.hesperides.tests.bdd.platforms.scenarios;

import cucumber.api.java8.En;
import org.apache.commons.lang3.StringUtils;
import org.hesperides.core.presentation.io.platforms.PlatformIO;
import org.hesperides.tests.bdd.modules.ModuleBuilder;
import org.hesperides.tests.bdd.platforms.PlatformBuilder;
import org.hesperides.tests.bdd.platforms.PlatformClient;
import org.hesperides.tests.bdd.templatecontainers.builders.ModelBuilder;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.containsString;
import static org.hesperides.tests.bdd.commons.StepHelper.assertOK;
import static org.hesperides.tests.bdd.commons.StepHelper.getResponseType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CreatePlatforms implements En {

    @Autowired
    private PlatformClient platformClient;
    @Autowired
    private PlatformBuilder platformBuilder;
    @Autowired
    private ModuleBuilder moduleBuilder;
    @Autowired
    private ModelBuilder modelBuilder;

    private ResponseEntity responseEntity;

    public CreatePlatforms() {

        Given("^an existing platform( with global properties)?( with valued properties)?( (?:and|with) this module)?$", (
                final String withGlobalProperties, final String withValuedProperties, final String withThisModule) -> {

            if (StringUtils.isNotEmpty(withThisModule)) {
                platformBuilder.withModule(moduleBuilder.build(), moduleBuilder.getPropertiesPath());
            }
            platformClient.create(platformBuilder.buildInput());

            if (StringUtils.isNotEmpty(withValuedProperties)) {
                platformBuilder.withProperty("module-foo", "12");
                platformBuilder.withProperty("techno-foo", "12");
                platformClient.saveProperties(platformBuilder.buildInput(), platformBuilder.buildPropertiesInput(), moduleBuilder.getPropertiesPath());
                platformBuilder.incrementVersionId();
            }

            if (StringUtils.isNotEmpty(withGlobalProperties)) {
                platformBuilder.withGlobalProperty("global-module-foo", "12", modelBuilder);
                platformBuilder.withGlobalProperty("global-techno-foo", "12", modelBuilder);
                platformBuilder.withGlobalProperty("unused-global-property", "12", modelBuilder);
                platformClient.saveGlobalProperties(platformBuilder.buildInput(), platformBuilder.buildPropertiesInput());
                platformBuilder.incrementVersionId();
            }
        });

        Given("^a platform to create(?:, named \"([^\"]*)\")?$", (final String name) -> {
            if (StringUtils.isNotEmpty(name)) {
                platformBuilder.withPlatformName(name);
            }
        });

        When("^I( try to)? create this platform$", (final String tryTo) -> {
            responseEntity = platformClient.create(platformBuilder.buildInput(), getResponseType(tryTo, PlatformIO.class));
        });

        Then("^the platform is successfully created$", () -> {
            assertOK(responseEntity);
            PlatformIO expectedPlatform = platformBuilder.buildOutput();
            PlatformIO actualPlatform = (PlatformIO) responseEntity.getBody();
            Assert.assertEquals(expectedPlatform, actualPlatform);
        });

        Then("^a ([45][0-9][0-9]) error is returned, blaming \"([^\"]+)\"$", (Integer httpCode, String message) -> {
            assertEquals(HttpStatus.valueOf(httpCode), responseEntity.getStatusCode());
            assertThat((String) responseEntity.getBody(), containsString(message));
        });
    }
}
