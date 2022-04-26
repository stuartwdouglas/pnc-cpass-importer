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

import java.util.Map;

/**
 * Build environment that builds are run in.
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = Environment.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Environment implements DTOEntity {

    /**
     * ID of the build environment.
     */
    private final String id;

    /**
     * Environment name.
     */
    private final String name;

    /**
     * Environment description.
     */
    private final String description;

    /**
     * The URL of the repository which contains the build system image.
     */
    private final String systemImageRepositoryUrl;

    /**
     * A unique identifier representing the system image, for example a Docker container ID or a checksum of a VM image.
     */
    private final String systemImageId;

    /**
     * Map of environment attributes.
     */
    private final Map<String, String> attributes;

    /**
     * Type of the build environment system image which will be used for the build.
     */
    private final SystemImageType systemImageType;

    /**
     * Is the environment deprecated and no longer advisable to be used by new builds?
     */
    private final boolean deprecated;

    /**
     * Is the environment to be hidden and not available anymore to user?
     */
    private final boolean hidden;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
