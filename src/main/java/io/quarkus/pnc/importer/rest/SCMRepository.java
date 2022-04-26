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

/**
 * Configuration of the SCM repository.
 *
 * @author Jakub Bartecek &lt;jbartece@redhat.com&gt;
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@JsonDeserialize(builder = SCMRepository.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SCMRepository implements DTOEntity {

    /**
     * ID of the SCM Repository.
     */
    protected final String id;

    /**
     * URL to the internal SCM repository, which is the main repository used for the builds. New commits can be added to
     * this repository, during the pre-build steps of the build process.
     */
    protected final String internalUrl;

    /**
     * URL to the upstream SCM repository.
     */
    protected final String externalUrl;

    /**
     * Declares whether the pre-build repository synchronization from external repository should happen or not.
     */
    protected final Boolean preBuildSyncEnabled;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
