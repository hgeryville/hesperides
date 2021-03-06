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
package org.hesperides.core.presentation.io.platforms;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.hesperides.core.domain.platforms.entities.Platform;
import org.hesperides.core.domain.platforms.queries.views.PlatformView;
import org.hesperides.core.presentation.io.OnlyPrintableCharacters;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Value
@AllArgsConstructor
public class PlatformIO {

    @OnlyPrintableCharacters(subject = "platform_name")
    @SerializedName("platform_name")
    String platformName;
    @OnlyPrintableCharacters(subject = "application_name")
    @SerializedName("application_name")
    String applicationName;
    @OnlyPrintableCharacters(subject = "version")
    String version;
    @SerializedName("production")
    boolean isProductionPlatform;
    @SerializedName("modules")
    List<DeployedModuleIO> deployedModules;
    @SerializedName("version_id")
    Long versionId;

    public PlatformIO(PlatformView platformView) {
        this(platformView, false);
    }

    public PlatformIO(PlatformView platformView, boolean hidePlatformsModules) {
        platformName = platformView.getPlatformName();
        applicationName = platformView.getApplicationName();
        version = platformView.getVersion();
        isProductionPlatform = platformView.isProductionPlatform();
        deployedModules = hidePlatformsModules ? Collections.emptyList() : DeployedModuleIO.fromDeployedModuleViews(platformView.getDeployedModules());
        versionId = platformView.getVersionId();
    }

    public Platform toDomainInstance() {
        return new Platform(
                new Platform.Key(applicationName, platformName),
                version,
                isProductionPlatform,
                versionId,
                DeployedModuleIO.toDomainInstances(deployedModules)
        );
    }

    public static List<PlatformIO> fromPlatformViews(List<PlatformView> platformViews, boolean hidePlatformsModules) {
        return Optional.ofNullable(platformViews)
                .orElse(Collections.emptyList())
                .stream()
                .map(platformView -> new PlatformIO(platformView, hidePlatformsModules))
                .collect(toList());
    }
}
