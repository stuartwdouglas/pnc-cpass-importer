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
 * Represents a released version of a product. For example, a Beta, GA, or SP release. Each release is associated with a
 * single product milestone.
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = ProductRelease.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRelease extends ProductReleaseRef {

    /**
     * Version of product this is release of.
     */
    
    private final ProductVersionRef productVersion;

    /**
     * Milestone that was released.
     */
    private final ProductMilestoneRef productMilestone;

    @lombok.Builder(builderClassName = "Builder", toBuilder = true)
    private ProductRelease(
            ProductVersionRef productVersion,
            ProductMilestoneRef productMilestone,
            String id,
            String version,
            SupportLevel supportLevel,
            Instant releaseDate,
            String commonPlatformEnumeration,
            String productPagesCode) {
        super(id, version, supportLevel, releaseDate, commonPlatformEnumeration, productPagesCode);
        this.productVersion = productVersion;
        this.productMilestone = productMilestone;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
