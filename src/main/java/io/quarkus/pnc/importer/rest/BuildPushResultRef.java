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
 * Result of a build push operation.
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
@Data
@Builder(builderClassName = "Builder", builderMethodName = "refBuilder")
@JsonDeserialize(builder = BuildPushResultRef.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildPushResultRef implements DTOEntity {

    /**
     * ID of the operation result.
     */

    
    protected final String id;

    /**
     * ID of the build pushed.
     */

    protected final String buildId;

    /**
     * Status of the build push.
     */

    protected final BuildPushStatus status;

    /**
     * Build id assigned by brew.
     */
    protected final Integer brewBuildId;

    /**
     * Link to Brew.
     */
    protected final String brewBuildUrl;

    /**
     * Identificator of log context. Logs related to this operation will have this log context id set.
     */
    protected final String logContext;

    /**
     * Used by group push, to describe rejected and error push request (should be used only for non-stored results).
     */
    protected final String message;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
