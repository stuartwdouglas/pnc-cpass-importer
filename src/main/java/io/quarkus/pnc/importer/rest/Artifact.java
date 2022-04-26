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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

/**
 * An artifact created or used by build.
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = Artifact.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifact extends ArtifactRef {

    /**
     * Repository that stores this artifact.
     */
    private final TargetRepository targetRepository;

    /**
     * Build that produced the artifact.
     */
    private final Build build;

    /**
     * The user who created this artifact.
     */
    private final User creationUser;

    /**
     * The user who last modified the quality level of this artifact.
     */
    private final User modificationUser;

    @lombok.Builder(builderClassName = "Builder", toBuilder = true)
    private Artifact(
            TargetRepository targetRepository,
            Build build,
            String id,
            String identifier,
            String purl,
            ArtifactQuality artifactQuality,
            BuildCategory buildCategory,
            String md5,
            String sha1,
            String sha256,
            String filename,
            String deployPath,
            Instant importDate,
            String originUrl,
            Long size,
            String deployUrl,
            String publicUrl,
            User creationUser,
            User modificationUser,
            Instant creationTime,
            Instant modificationTime,
            String qualityLevelReason) {
        super(
                id,
                identifier,
                purl,
                artifactQuality,
                buildCategory,
                md5,
                sha1,
                sha256,
                filename,
                deployPath,
                importDate,
                originUrl,
                size,
                deployUrl,
                publicUrl,
                creationTime,
                modificationTime,
                qualityLevelReason);
        this.targetRepository = targetRepository;
        this.build = build;
        this.creationUser = creationUser;
        this.modificationUser = modificationUser;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
