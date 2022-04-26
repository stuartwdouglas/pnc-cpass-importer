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
 * Contains information related to a repository of build artifacts (i.e. Maven, NPM, etc).
 *
 * @author Jakub Bartecek &lt;jbartece@redhat.com&gt;
 */
@Data
@Builder(builderClassName = "Builder", builderMethodName = "refBuilder")
@JsonDeserialize(builder = TargetRepository.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TargetRepository implements DTOEntity {
    /**
     * ID of the target repository.
     */
    protected final String id;

    /**
     * Is the reposiotry for temporary builds?
     */
    protected final Boolean temporaryRepo;

    /**
     * Identifier to link repository configurations (eg. hostname)
     */
    protected final String identifier;

    /**
     * The type of repository which hosts this artifact (Maven, NPM, etc). This field determines the format of the
     * identifier string.
     */
    protected final RepositoryType repositoryType;

    /**
     * Path that needs to be appended to the hostname eg. "ga" for https://maven.repository.redhat.com/ga/ or "maven2"
     * for https://repo1.maven.org/maven2/ or "" (empty string) when the repository content starts at root
     */
    protected final String repositoryPath;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
