/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014-2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.pnc.importer.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;


/**
 * A build config cointains the information needed to execute a build of a project, i.e. link to the sources, the build
 * script, the build system image needed to run.
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
@Data
@Builder(builderClassName = "Builder", builderMethodName = "refBuilder")
@JsonDeserialize(builder = BuildConfigurationRef.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildConfigurationRef implements DTOEntity {

    /**
     * ID of the build config.
     */
    protected final String id;

    /**
     * Build config name. It must be unique and can be made of alphanumeric characters with [_.-].
     */
    protected final String name;

    /**
     * Build config description.
     */

    protected final String description;

    /**
     * Shell script to be executed.
     */

    protected final String buildScript;

    /**
     * SCM revision to build.
     */

    protected final String scmRevision;

    /**
     * The time when the build config was created.
     */
    protected final Instant creationTime;

    /**
     * The time when the build config was last modified.
     */
    protected final Instant modificationTime;

    /**
     * Build type of the build config. It defines pre-build operations and sets the proper repository.
     */


    protected final BuildType buildType;

    /**
     * The default alignment parameters for this build config type.
     */
    protected final String defaultAlignmentParams;

    /**
     * Indicates whether the Brew Bridge Pull feature is active or not. It defaults to false.
     */

    protected final Boolean brewPullActive;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
