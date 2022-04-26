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
 * A milestone represents a stage in the product(ization) process. A single product version, for example "1.0", can be
 * associated with several product milestones such as "1.0.0.build1", "1.0.0.build2", etc. A milestone represents the
 * set of work (build records) that was performed during a development cycle from the previous milestone until the end
 * of the current milestone.
 *
 * @author Jakub Bartecek &lt;jbartece@redhat.com&gt;
 */
@Data
@Builder(builderClassName = "Builder", builderMethodName = "refBuilder")
@JsonDeserialize(builder = ProductMilestoneRef.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductMilestoneRef implements DTOEntity {

    /**
     * ID of the product milestone.
     */

    
    protected final String id;

    /**
     * Milestone version.
     */
    protected final String version;

    /**
     * The time when the work on the milestone ended. If the endDate is set, the milestone is closed and no new content
     * can be added to it.
     */

    
    protected final Instant endDate;

    /**
     * The scheduled starting date of this milestone.
     */

    protected final Instant startingDate;

    /**
     * The scheduled ending date of this milestone.
     */

    protected final Instant plannedEndDate;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
